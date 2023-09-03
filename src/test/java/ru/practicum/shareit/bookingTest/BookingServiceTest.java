package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

class BookingServiceTest {
    private static UserService userService;
    private static ItemService itemService;
    private static BookingRepository bookingRepository;
    private static BookingService bookingService;
    private static User owner;
    private static User booker;
    private static User wrongBooker;
    private static Booking booking;
    private static Item item;
    private static Item wrongItem;
    private static List<Booking> bookings;

    @BeforeEach
    void beforeAll() {
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);
        owner = new User(1L, "user1@email.ru", "user1");
        booker = new User(2L, "user2@email.ru", "user2");
        wrongBooker = new User(9999L, "user9999@email.ru", "user9999");
        item = new Item(1L, "first item", "new item", true, owner, null);
        wrongItem = new Item(9999L, "wrong item", "new item", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                item, booker, APPROVED);
        bookings = new LinkedList<>(List.of(booking));
    }

    @Test
    void createBookingTest() {
        Long itemId = booking.getId();
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        when(userService.userIsExistsById(booker.getId()))
                .thenReturn(true);
        when(itemService.itemIsExistsById(itemId))
                .thenReturn(true);
        when(itemService.itemIsAvailableById(itemId))
                .thenAnswer(invocationOnMock -> {
                    if (item.getAvailable()) {
                        return true;
                    } else {
                        return false;
                    }
                });
        when(itemService.getItemById(itemId))
                .thenReturn(item);
        assertEquals(booking, bookingService.createBooking(booking));
        booking.setBooker(wrongBooker);
        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе пользователя с некорректным id ");
        booking.setBooker(booker);
        booking.setItem(wrongItem);
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе вещи с некорректным id ");
        booking.setItem(item);
        item.setAvailable(false);
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе вещи недоступной для бронирования");
        item.setAvailable(true);
        booking.setStart(LocalDateTime.now().minusMonths(1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректной датой начала бронирования");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(LocalDateTime.now().minusMonths(1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректной датой окончания брони");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(booking.getStart());
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректными датами начала " +
                        "и окончания бронирования");
        booking.setStart(LocalDateTime.now().plusMonths(2));
        booking.setEnd(LocalDateTime.now().plusMonths(1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректными датами начала " +
                        "и окончания бронирования");
        booking.setEnd(LocalDateTime.now().plusMonths(3L));
        owner = booker;
        item.setOwner(booker);
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе бронирования владельцем вещи");
    }

    @Test
    void considerationOfBookingTest() {
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        when(bookingRepository.getReferenceById(booking.getId()))
                .thenReturn(booking);
        when(itemService.getItemById(item.getId()))
                .thenReturn(item);
        assertThrows(IncorrectParameterException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                item.getOwner().getId(), true), "Метод considerationOfBooking работает некорректно");
        booking.setApproved(BookingStatus.WAITING);
        assertEquals(booking, bookingService.considerationOfBooking(booking.getId(),
                item.getOwner().getId(), true));
        assertEquals(booking, bookingService.considerationOfBooking(booking.getId(),
                item.getOwner().getId(), false));
        assertThrows(UserNotFoundException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                booker.getId(), true), "Метод considerationOfBooking работает некорректно");
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.getReferenceById(booking.getId()))
                .thenReturn(booking);
        when(bookingRepository.existsById(booking.getId()))
                .thenReturn(true);
        when(itemService.getItemById(item.getId()))
                .thenReturn(item);
        assertEquals(booking, bookingService.getBookingById(booker.getId(), booking.getId()));
        assertEquals(booking, bookingService.getBookingById(owner.getId(), booking.getId()));
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(booker.getId(), 2L),
                "Метод getBookingByIdTest работает некорректно при попытке получить несуществующую бронь");
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(wrongBooker.getId(),
                        booking.getId()),
                "Метод getBookingByIdTest работает некорректно при попытке получить бронь пользователем, " +
                        "ее не создавшим");
    }

    @Test
    void findLastBooking() {
        when(bookingRepository.findLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(booking);
        assertEquals(booking, bookingService.findLastBooking(item.getId(), LocalDateTime.now()));
    }

    @Test
    void findNextBooking() {
        when(bookingRepository.findNextBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(booking);
        assertEquals(booking, bookingService.findNextBooking(item.getId(), LocalDateTime.now()));
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        when(bookingRepository.getAllByItemOwnerId(eq(owner.getId()), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByItemOwnerIdAndApproved(eq(owner.getId()), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByItemOwnerIdAndStartBeforeAndEndAfter(eq(owner.getId()), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByItemOwnerIdAndEndBefore(eq(owner.getId()), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);
        when(userService.userIsExistsById(owner.getId()))
                .thenReturn(true);
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), null, (short) 0, (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "WAITING", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "REJECTED", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "APPROVED", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "FUTURE", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "ALL", (short) 0, (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "CURRENT", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByOwnerId(owner.getId(), "PAST", (short) 0,
                (short) 30));
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByOwnerId(wrongBooker.getId(),
                        null, (short) 0, (short) 30),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони пользователя, " +
                        "которого не существует");
        assertThrows(IncorrectParameterException.class, () ->
                        bookingService.getAllBookingByOwnerId(owner.getId(), "UNKNOWN", (short) 0, (short) 30),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони с " +
                        "неизвестным статусом");
    }

    @Test
    void getAllBookingByUserIdTest() {
        when(bookingRepository.getAllByBookerId(eq(booker.getId()), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByBookerIdAndApproved(eq(booker.getId()), any(BookingStatus.class),
                any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByBookerIdAndStartBeforeAndEndAfter(eq(booker.getId()), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByBookerIdAndEndBefore(eq(booker.getId()), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(
                eq(booker.getId()), eq(item.getId()), any(BookingStatus.class), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookings);
        when(userService.userIsExistsById(booker.getId()))
                .thenReturn(true);
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), null, (short) 0, (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "WAITING", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "REJECTED", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "APPROVED", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "FUTURE", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "ALL", (short) 0, (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "CURRENT", (short) 0,
                (short) 30));
        assertEquals(bookings, bookingService.getAllBookingByUserId(booker.getId(), "PAST", (short) 0,
                (short) 30));
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByUserId(wrongBooker.getId(),
                        null, (short) 0, (short) 30),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони пользователя, " +
                        "которого не существует");
        assertThrows(IncorrectParameterException.class, () ->
                        bookingService.getAllBookingByUserId(booker.getId(), "UNKNOWN", (short) 0, (short) 30),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони с " +
                        "неизвестным статусом");
    }
}