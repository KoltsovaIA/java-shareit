package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OutgoingBookingDto {
    private Long id;
    private ShortItemDto item;
    private ShortBookerDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}