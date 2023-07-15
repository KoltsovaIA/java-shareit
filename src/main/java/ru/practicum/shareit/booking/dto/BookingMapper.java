package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingMapper {
    private final ItemService itemService;
    private final UserService userService;

    public Booking dtoToBooking(Long bookerId, IncomingBookingDto incomingBookingDto) {
        return Booking.builder()
                .id(null)
                .bookerId(bookerId)
                .itemId(incomingBookingDto.getItemId())
                .start(incomingBookingDto.getStart())
                .end(incomingBookingDto.getEnd())
                .approved(BookingStatus.WAITING)
                .build();
    }

    public OutgoingBookingDto bookingToDto(Booking booking) {
        return OutgoingBookingDto.builder()
                .id(booking.getId())
                .booker(userService.getUserById(booking.getBookerId()))
                .item(itemService.getItemById(booking.getItemId()))
                .status(booking.getApproved())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public List<OutgoingBookingDto> listItemToListDto(List<Booking> bookings) {
        LinkedList<OutgoingBookingDto> listBookingDto = new LinkedList<>();
        bookings.forEach(value -> listBookingDto.add(bookingToDto(value)));
        return listBookingDto;
    }
}