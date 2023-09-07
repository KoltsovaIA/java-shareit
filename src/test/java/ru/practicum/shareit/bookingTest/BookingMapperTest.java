package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BookingMapperTest {
    private static ItemService itemService;
    private static UserService userService;
    private static BookingMapper bookingMapper;
    private static Item item;
    private static User booker;
    private static User requester;
    private static IncomingBookingDto incomingBookingDto;
    private static ItemRequest itemRequest;
    private static Booking booking;
    private static Booking bookingFromDto;
    private static OutgoingBookingDto bookingDto;

    @BeforeEach
    void beforeAll() {
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        bookingMapper = new BookingMapper(itemService, userService);
        User user = new User(5L, "user@email.ru", "User1");
        booker = new User(1L, "user2@email.ru", "User2");
        ShortBookerDto shortBooker = new ShortBookerDto(1L);
        item = new Item(1L, "name", "description", true, user, itemRequest);
        ShortItemDto shortItem = new ShortItemDto(1L, "name");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), requester);
        incomingBookingDto = new IncomingBookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        booking = new Booking(1L, incomingBookingDto.getStart(), incomingBookingDto.getEnd(), item, booker,
                BookingStatus.WAITING);
        bookingFromDto = new Booking(null, incomingBookingDto.getStart(), incomingBookingDto.getEnd(), item, booker,
                BookingStatus.WAITING);
        requester = new User(100L, "user100@email.ru", "User100");
        bookingDto = new OutgoingBookingDto(1L, shortItem, shortBooker, booking.getStart(), booking.getEnd(),
                BookingStatus.WAITING);
    }

    @Test
    void dtoToBookingTest() {
        when(userService.getUserById(booker.getId()))
                .thenReturn(booker);
        when(itemService.getItemById(incomingBookingDto.getItemId()))
                .thenReturn(item);
        assertThat(bookingMapper.dtoToBooking(booker.getId(), incomingBookingDto))
                .hasFieldOrPropertyWithValue("id", bookingFromDto.getId())
                .hasFieldOrPropertyWithValue("start", bookingFromDto.getStart())
                .hasFieldOrPropertyWithValue("end", bookingFromDto.getEnd())
                .hasFieldOrPropertyWithValue("item", bookingFromDto.getItem())
                .hasFieldOrPropertyWithValue("booker", bookingFromDto.getBooker())
                .hasFieldOrPropertyWithValue("approved", bookingFromDto.getApproved());
    }

    @Test
    void bookingToDtoTest() {
        assertThat(bookingMapper.bookingToDto(booking))
                .hasFieldOrPropertyWithValue("id", bookingDto.getId())
                .hasFieldOrPropertyWithValue("start", bookingDto.getStart())
                .hasFieldOrPropertyWithValue("end", bookingDto.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingDto.getStatus())
                .satisfies(OutgoingBookingDto -> {
                    assertThat(OutgoingBookingDto.getItem()).hasFieldOrPropertyWithValue("id",
                            bookingDto.getItem().getId());
                    assertThat(OutgoingBookingDto.getItem()).hasFieldOrPropertyWithValue("name",
                            bookingDto.getItem().getName());
                })
                .satisfies(OutgoingBookingDto -> assertThat(OutgoingBookingDto.getBooker())
                        .hasFieldOrPropertyWithValue("id", bookingDto.getBooker().getId()));
    }

    @Test
    void listBookingToListDtoTest() {
        LinkedList<Booking> listBooking = new LinkedList<>();
        listBooking.add(booking);
        assertThat(bookingMapper.listBookingToListDto(listBooking))
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", bookingDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("start", bookingDto.getStart());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("end", bookingDto.getEnd());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("status", bookingDto.getStatus());
                    assertThat(list.get(0)).satisfies(OutgoingBookingDto -> {
                        assertThat(OutgoingBookingDto.getItem()).hasFieldOrPropertyWithValue("id",
                                bookingDto.getItem().getId());
                        assertThat(OutgoingBookingDto.getItem()).hasFieldOrPropertyWithValue("name",
                                bookingDto.getItem().getName());
                    });
                    assertThat(list.get(0)).satisfies(OutgoingBookingDto -> assertThat(OutgoingBookingDto.getBooker())
                            .hasFieldOrPropertyWithValue("id", bookingDto.getBooker().getId()));
                });
    }
}