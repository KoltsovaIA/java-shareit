package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEndDateValid
public class IncomingBookingDto {
    @NotNull(message = "ID вещи должно быть указано")
    @Min(1)
    private long itemId;

    @NotNull(message = "Время начала бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    @Future
    private LocalDateTime end;
}