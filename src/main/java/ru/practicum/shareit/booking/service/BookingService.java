package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking considerationOfBooking(Long bookingId, Long ownerId, boolean approved);

    Booking getBookingById(Long booker, Long bookingId);

    List<Booking> getBookingsByBooker(Long bookerId);

    List<Booking> getBookingsByOwner(Long ownerId);


    Booking findLastBooking(Long itemId, LocalDateTime start);

    Booking findNextBooking(Long itemId, LocalDateTime start);

    List<Booking> getBookingsByBookerAndState(Long bookerId, BookingStatus state);

    List<Booking> getBookingsByOwnerAndState(Long ownerId, BookingStatus state);

    List<Booking> getCurrentBookingsByBooker(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> getPastBookingsByBooker(Long bookerId, LocalDateTime time);

    List<Booking> getPastBookingsByOwnerId(Long ownerId, LocalDateTime time);

    List<Booking> getCurrentBookingsByOwnerId(Long ownerId, LocalDateTime time);
}