package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.bookingDto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    OutgoingBookingDto createBooking(Long bookerId, IncomingBookingDto booking);

    OutgoingBookingDto considerationOfBooking(Long bookingId, Long ownerId, Boolean approved);

    OutgoingBookingDto getBookingById(Long booker, Long bookingId);

    Booking findLastBooking(Long itemId, LocalDateTime start);

    Booking findNextBooking(Long itemId, LocalDateTime start);

    List<OutgoingBookingDto> getAllBookingByOwnerId(Long ownerId, String state, Short from, Short size);

    List<OutgoingBookingDto> getAllBookingByUserId(Long userId, String state, Short from, Short size);
}