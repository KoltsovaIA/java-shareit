package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "booking_start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "booking_end_time", nullable = false)
    private LocalDateTime end;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "booker_id", nullable = false)
    private Long bookerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approved", length = 20, nullable = false)
    private BookingStatus approved;
}