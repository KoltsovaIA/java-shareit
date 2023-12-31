package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getAllByBookerId(Long bookerId, Pageable page);

    List<Booking> getAllByBookerIdAndApproved(Long bookerId, BookingStatus state,
                                                    Pageable page);

    List<Booking> getAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start,
                                                                  LocalDateTime end, Pageable page);

    List<Booking> getAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(Long bookerId, Long itemId,
                                                                                         BookingStatus state,
                                                                                         LocalDateTime end,
                                                                                         Pageable pageable);

    List<Booking> getAllByItemOwnerId(Long ownerId, Pageable page);

    List<Booking> getAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime time, Pageable page);

    List<Booking> getAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime time,
                                                                     LocalDateTime time2, Pageable page);

    List<Booking> getAllByItemOwnerIdAndApproved(Long ownerId, BookingStatus state, Pageable page);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND NOT approved='REJECTED' AND booking_start_time < ?2 "
            + "ORDER BY booking_start_time DESC LIMIT 1 ", nativeQuery = true)
    Booking findLastBooking(Long itemId, LocalDateTime start);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND NOT approved='REJECTED' AND booking_start_time > ?2 "
            + "ORDER BY booking_start_time ASC LIMIT 1 ", nativeQuery = true)
    Booking findNextBooking(Long itemId, LocalDateTime start);
}