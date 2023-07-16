package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking considerationOfBooking(Long bookingId, Long ownerId, boolean approved);

    Booking getBookingById(Long booker, Long bookingId);

    Booking findLastBooking(Long itemId, LocalDateTime start);

    Booking findNextBooking(Long itemId, LocalDateTime start);

    List<Booking> getAllBookingByOwnerId(Long ownerId, String state);

    List<Booking> getAllBookingByUserId(Long userId, String state);
}