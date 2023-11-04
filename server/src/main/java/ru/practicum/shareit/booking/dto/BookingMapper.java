package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.LinkedList;
import java.util.List;

public class BookingMapper {
    private BookingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static OutgoingBookingDto bookingToDto(Booking booking) {
        return OutgoingBookingDto.builder()
                .id(booking.getId())
                .booker(ShortBookerDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(ShortItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .status(booking.getApproved())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static List<OutgoingBookingDto> listBookingToListDto(List<Booking> bookings) {
        List<OutgoingBookingDto> listBookingDto = new LinkedList<>();
        bookings.forEach(value -> listBookingDto.add(bookingToDto(value)));
        return listBookingDto;
    }
}