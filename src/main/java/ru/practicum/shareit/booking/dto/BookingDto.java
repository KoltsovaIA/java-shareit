package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private long bookerId;
    private BookingStatus status;

    @NotBlank(message = "ID вещи должно быть указано")
    private long itemId;

    @NotBlank(message = "Время начала бронирования должно быть указано")
    private LocalDateTime start;

    @NotBlank(message = "Время окончания бронирования должно быть указано")
    private LocalDateTime end;
    private String itemName;
}