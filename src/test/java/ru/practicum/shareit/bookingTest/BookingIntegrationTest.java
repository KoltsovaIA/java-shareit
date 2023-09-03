package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private static User owner;
    private static User booker;
    private static Item item;
    private static Item item2;
    private static Booking booking;
    private static Booking booking1;

    @BeforeAll
    static void beforeAll() {
        owner = User.builder()
                .id(1L)
                .name("name")
                .email("user1@email.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("name2")
                .email("user2@email.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .itemRequest(null)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(owner)
                .itemRequest(null)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMonths(1))
                .end(LocalDateTime.now().plusMonths(2))
                .item(item)
                .booker(booker)
                .approved(BookingStatus.WAITING)
                .build();
        booking1 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMonths(3))
                .end(LocalDateTime.now().plusMonths(4))
                .item(item2)
                .booker(booker)
                .approved(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBookingTest() {
        userService.createUser(owner);
        userService.createUser(booker);
        itemService.createItem(item);
        bookingService.createBooking(booking);
        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(1L, equalTo(bookingFromDb.getItem().getId()));
        assertThat(2L, equalTo(bookingFromDb.getBooker().getId()));
        assertThat(BookingStatus.WAITING, equalTo(bookingFromDb.getApproved()));
    }

    @Test
    void approveBookingTest() {
        userService.createUser(owner);
        userService.createUser(booker);
        itemService.createItem(item);
        bookingService.createBooking(booking);
        bookingService.considerationOfBooking(1L, 1L, true);
        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(1L, equalTo(bookingFromDb.getItem().getId()));
        assertThat(2L, equalTo(bookingFromDb.getBooker().getId()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingFromDb.getApproved()));
    }

    @Test
    void shouldGetBookingByUserOwner() {
        userService.createUser(owner);
        userService.createUser(booker);
        itemService.createItem(item);
        bookingService.createBooking(booking);
        bookingService.considerationOfBooking(1L, 1L, true);
        Booking bookingById = bookingService.getBookingById(1L, 1L);
        assertThat(1L, equalTo(bookingById.getItem().getId()));
        assertThat(2L, equalTo(bookingById.getBooker().getId()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingById.getApproved()));
    }

    @Test
    void getAllBookingByUserIdTest() {
        userService.createUser(owner);
        userService.createUser(booker);
        itemService.createItem(item);
        itemService.createItem(item2);
        bookingService.createBooking(booking);
        bookingService.createBooking(booking1);
        bookingService.considerationOfBooking(1L, 1L, true);
        List<Booking> bookings = bookingService.getAllBookingByUserId(booker.getId(), "ALL", (short) 0, (short) 3);
        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        userService.createUser(owner);
        userService.createUser(booker);
        itemService.createItem(item);
        itemService.createItem(item2);
        bookingService.createBooking(booking);
        bookingService.createBooking(booking1);
        bookingService.considerationOfBooking(1L, owner.getId(), true);
        bookingService.considerationOfBooking(2L, owner.getId(), true);
        List<Booking> bookings = bookingService.getAllBookingByOwnerId(1L, "ALL", (short) 0, (short) 2);
        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
    }
}