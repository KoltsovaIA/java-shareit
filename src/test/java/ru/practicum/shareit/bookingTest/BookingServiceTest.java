/*
package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

public class BookingServiceTest {
    UserService userService = Mockito.mock(UserService.class);
    ItemService itemService = Mockito.mock(ItemService.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @InjectMocks
    BookingService bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);
    private Long owner;
    private Long booker;
    private Booking booking;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = 1L;
        booker = 2L;
        item = new Item(1L, "first item", "new item", true, 1L);
        booking = new Booking(1L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                1L, 2L, APPROVED);
    }

    @Test
    void createBookingTest(){
        Long itemId = booking.getItemId();
        Mockito.when(bookingRepository.save(any()))
                        .thenReturn(booking);
        Mockito.when(userService.userIsExistsById(booker))
                        .thenReturn(true);
        Mockito.when(itemService.itemIsExistsById(item.getId()))
                        .thenReturn(true);
        Mockito.when(itemService.itemIsAvailableById(itemId))
                        .thenReturn(true);
        Mockito.when(itemService.getItemById(itemId))
                .thenReturn(item);

        assertEquals(bookingService.createBooking(booking), booking);
        booking.setBookerId(5L);
        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе пользователя с некорректным id ");
        booking.setBookerId(2L);
        booking.setItemId(6L);
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе вещи с некорректным id ");
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now().minusMonths(1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректной датой начала бронирования");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(LocalDateTime.now().minusMonths(1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.createBooking(booking),
                "Метод createBooking работает некорректно при запросе с некоректной датой окончания брони");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(LocalDateTime.now().plusMonths(1));
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
    void considerationOfBookingTest(){
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);
        Mockito.when(bookingRepository.getReferenceById(1L))
                        .thenReturn(booking);
        Mockito.when(itemService.getItemById(1L))
                .thenReturn(item);
        assertThrows(IncorrectParameterException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                item.getOwner(), true), "Метод considerationOfBooking работает некорректно");
        booking.setApproved(BookingStatus.WAITING);
        assertEquals(bookingService.considerationOfBooking(booking.getId(), item.getOwner(), true), booking);
        assertThrows(IncorrectParameterException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                        item.getOwner(), true), "Метод considerationOfBooking работает некорректно");
        assertThrows(IncorrectParameterException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                item.getOwner(), true), "Метод considerationOfBooking работает некорректно");
        booking.setApproved(BookingStatus.WAITING);
        assertEquals(bookingService.considerationOfBooking(booking.getId(), item.getOwner(), true), booking);
        item.setAvailable(false);
        assertThrows(IncorrectParameterException.class, () -> bookingService.considerationOfBooking(booking.getId(),
                item.getOwner(), true), "Метод considerationOfBooking работает некорректно");
    }

    @Test
    void getBookingByIdTest(){
        Mockito.when(bookingRepository.getReferenceById(1L))
                .thenReturn(booking);
        Mockito.when(bookingRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(itemService.getItemById(1L))
                .thenReturn(item);
        assertEquals(bookingService.getBookingById(booker, 1L), booking);
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(booker, 2L),
                "Метод getBookingByIdTest работает некорректно при попытке получить несуществующую бронь");
        booker = 3L;
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(booker, 2L),
                "Метод getBookingByIdTest работает некорректно при попытке получить бронь пользователем, " +
                        "ее не создавшим");
        booker = 2L;
        owner = 3L;
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(booker, 2L),
                "Метод getBookingByIdTest работает некорректно при попытке получить бронь вещи " +
                        "не ее владельцем");
    }

    @Test
    void findLastBooking(){
        Mockito.when(bookingRepository.findLastBooking(anyLong(), any()))
                .thenReturn(booking);
        assertEquals(bookingService.findLastBooking(1L, LocalDateTime.now()), booking);

    }

    @Test
    void findNextBooking(){
        Mockito.when(bookingRepository.findNextBooking(anyLong(), any()))
                .thenReturn(booking);
        assertEquals(bookingService.findNextBooking(1L, LocalDateTime.now()), booking);
    }

    @Test
    void getAllBookingByOwnerIdTest(){
        Mockito.when(bookingRepository.getAllByOwnerId(owner))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getAllByOwnerAndState(eq(owner), anyString()))
                        .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getCurrentAllByOwnerId(eq(owner), any(LocalDateTime.class)))
                        .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getPastAllByOwnerId(eq(owner), any(LocalDateTime.class)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(userService.userIsExistsById(owner))
                        .thenReturn(true);
        assertEquals(bookingService.getAllBookingByOwnerId(owner, null), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "WAITING"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "REJECTED"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "APPROVED"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "FUTURE"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "ALL"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "CURRENT"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByOwnerId(owner, "PAST"), new LinkedList<>(List.of(booking)));
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByOwnerId(5L, null),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони пользователя, " +
                        "которого не существует");
        assertThrows(IncorrectParameterException.class, () ->
                        bookingService.getAllBookingByOwnerId(owner, "UNKNOWN"),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони с " +
                        "неизвестным статусом");

    }

    @Test
    void getAllBookingByUserIdTest(){
        Mockito.when(bookingRepository.getAllByBookerIdOrderByStartDesc(booker))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getAllByBookerIdAndApprovedOrderByStartDesc(eq(booker), any(BookingStatus.class)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getAllByBookerIdOrderByStartDesc(eq(booker)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker),
                        any(LocalDateTime.class),any(LocalDateTime.class)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(bookingRepository.getAllByBookerIdAndEndBeforeOrderByStartDesc(eq(booker),
                any(LocalDateTime.class)))
                        .thenReturn(new LinkedList<>(List.of(booking)));
        Mockito.when(userService.userIsExistsById(booker))
                .thenReturn(true);
        assertEquals(bookingService.getAllBookingByUserId(booker, null), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "WAITING"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "REJECTED"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "APPROVED"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "FUTURE"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "ALL"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "CURRENT"), new LinkedList<>(List.of(booking)));
        assertEquals(bookingService.getAllBookingByUserId(booker, "PAST"), new LinkedList<>(List.of(booking)));
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingByUserId(5L, null),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони пользователя, " +
                        "которого не существует");
        assertThrows(IncorrectParameterException.class, () ->
                        bookingService.getAllBookingByUserId(booker, "UNKNOWN"),
                "Метод getAllBookingByOwnerId работает некорректно при попытке получить брони с " +
                        "неизвестным статусом");
    }
}
*/
