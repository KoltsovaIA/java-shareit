package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomingBookingDto {
    @NotNull(message = "ID вещи должно быть указано")
    @Min(1)
    private long itemId;

    @NotNull(message = "Время начала бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime end;
}