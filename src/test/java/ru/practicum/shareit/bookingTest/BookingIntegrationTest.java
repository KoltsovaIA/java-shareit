package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
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
    private static UserDto ownerDto;
    private static User booker;
    private static UserDto bookerDto;
    private static IncomingItemDto itemDto;
    private static Item item;
    private static IncomingItemDto itemDto2;
    private static Item item2;
    private static IncomingBookingDto bookingDto;
    private static IncomingBookingDto bookingDto1;
    private static Booking booking;
    private static Booking booking1;

    @BeforeAll
    static void beforeAll() {
        ownerDto = UserDto.builder()
                .name("name")
                .email("user1@email.ru")
                .build();

        owner = User.builder()
                .id(1L)
                .name(ownerDto.getName())
                .email(ownerDto.getEmail())
                .build();

        bookerDto = UserDto.builder()
                .name("name2")
                .email("user2@email.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .build();

        itemDto = IncomingItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        item = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(null)
                .build();

        itemDto2 = IncomingItemDto.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .requestId(null)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name(itemDto2.getName())
                .description(itemDto2.getDescription())
                .available(itemDto2.getAvailable())
                .owner(owner)
                .itemRequest(null)
                .build();

        bookingDto = IncomingBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMonths(1))
                .end(LocalDateTime.now().plusMonths(2))
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .approved(BookingStatus.WAITING)
                .build();

        bookingDto1 = IncomingBookingDto.builder()
                .itemId(item2.getId())
                .start(LocalDateTime.now().plusMonths(3))
                .end(LocalDateTime.now().plusMonths(4))
                .build();

        booking1 = Booking.builder()
                .id(2L)
                .start(bookingDto1.getStart())
                .end(bookingDto1.getEnd())
                .item(item2)
                .booker(booker)
                .approved(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBookingTest() {
        userService.createUser(ownerDto);
        userService.createUser(bookerDto);
        itemService.createItem(owner.getId(), itemDto);
        bookingService.createBooking(booker.getId(), bookingDto);
        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(item.getId(), equalTo(bookingFromDb.getItem().getId()));
        assertThat(booker.getId(), equalTo(bookingFromDb.getBooker().getId()));
        assertThat(BookingStatus.WAITING, equalTo(bookingFromDb.getApproved()));
    }

    @Test
    void approveBookingTest() {
        userService.createUser(ownerDto);
        userService.createUser(bookerDto);
        itemService.createItem(owner.getId(), itemDto);
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.considerationOfBooking(1L, owner.getId(), true);
        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id",
                Booking.class);
        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(item.getId(), equalTo(bookingFromDb.getItem().getId()));
        assertThat(booker.getId(), equalTo(bookingFromDb.getBooker().getId()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingFromDb.getApproved()));
    }

    @Test
    void shouldGetBookingByUserOwner() {
        userService.createUser(ownerDto);
        userService.createUser(bookerDto);
        itemService.createItem(owner.getId(), itemDto);
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.considerationOfBooking(booking.getId(), owner.getId(), true);
        OutgoingBookingDto bookingById = bookingService.getBookingById(booker.getId(), booking.getId());
        assertThat(item.getId(), equalTo(bookingById.getItem().getId()));
        assertThat(booker.getId(), equalTo(bookingById.getBooker().getId()));
        assertThat(bookingDto.getStart(), equalTo(bookingById.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingById.getEnd()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingById.getStatus()));
    }

    @Test
    void getAllBookingByUserIdTest() {
        userService.createUser(ownerDto);
        userService.createUser(bookerDto);
        itemService.createItem(owner.getId(), itemDto);
        itemService.createItem(owner.getId(), itemDto2);
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.createBooking(booker.getId(), bookingDto1);
        bookingService.considerationOfBooking(booking.getId(), owner.getId(), true);
        List<OutgoingBookingDto> bookings = bookingService.getAllBookingByUserId(booker.getId(), "ALL", (short) 0,
                (short) 3);
        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        userService.createUser(ownerDto);
        userService.createUser(bookerDto);
        itemService.createItem(owner.getId(), itemDto);
        itemService.createItem(owner.getId(), itemDto2);
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.createBooking(booker.getId(), bookingDto1);
        bookingService.considerationOfBooking(booking.getId(), owner.getId(), true);
        bookingService.considerationOfBooking(booking1.getId(), owner.getId(), true);
        List<OutgoingBookingDto> bookings = bookingService.getAllBookingByOwnerId(1L, "ALL", (short) 0,
                (short) 2);
        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
    }
}