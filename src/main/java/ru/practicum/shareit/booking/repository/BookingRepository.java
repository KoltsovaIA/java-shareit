package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    LinkedList<Booking> getAllByBookerIdOrderByStartDesc(Long bookerId);

    LinkedList<Booking> getAllByBookerIdAndApprovedOrderByStartDesc(Long bookerId, BookingStatus state);

    LinkedList<Booking> getAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                                  LocalDateTime end);

    LinkedList<Booking> getAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    LinkedList<Booking> getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(Long bookerId, Long itemId,
                                                                                         BookingStatus state,
                                                                                         LocalDateTime end);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT item_id FROM items WHERE item_owner = ?1) "
            + "ORDER BY booking_start_time DESC", nativeQuery = true)
    LinkedList<Booking> getAllByOwner(Long ownerId);

    @Query(value = "SELECT * FROM bookings WHERE booking_end_time < ?2 "
            + "AND item_id IN (SELECT item_id FROM items WHERE item_owner = ?1) ORDER BY booking_start_time DESC",
            nativeQuery = true)
    LinkedList<Booking> getPastAllByOwnerId(Long ownerId, LocalDateTime time);

    @Query(value = "SELECT * FROM bookings WHERE booking_end_time > ?2 AND booking_start_time < ?2 "
            + "AND item_id IN (SELECT item_id FROM items WHERE item_owner = ?1) ORDER BY booking_start_time DESC",
            nativeQuery = true)
    LinkedList<Booking> getCurrentAllByOwnerId(Long ownerId, LocalDateTime time);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN (SELECT item_id FROM items WHERE item_owner = ?1) "
            + "AND approved = ?2 ORDER BY booking_start_time DESC", nativeQuery = true)
    LinkedList<Booking> getAllByOwnerAndState(Long ownerId, String state);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND NOT approved='REJECTED' AND booking_start_time < ?2 "
            + "ORDER BY booking_start_time DESC LIMIT 1 ", nativeQuery = true)
    Booking findLastBooking(Long itemId, LocalDateTime start);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ?1 AND NOT approved='REJECTED' AND booking_start_time > ?2 "
            + "ORDER BY booking_start_time ASC LIMIT 1 ", nativeQuery = true)
    Booking findNextBooking(Long itemId, LocalDateTime start);
}