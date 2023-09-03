package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingBookingDto {
    @NotNull(message = "ID бронирования должно быть указано")
    @Min(1)
    private Long id;

    @NotNull(message = "вещь должна быть указана")
    private ShortItemDto item;

    @NotNull(message = "заказчик должн быть указан")
    private ShortBookerDto booker;

    @NotNull(message = "Время начала бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime end;

    @NotNull(message = "статус бронирования должен быть указан")
    private BookingStatus status;
}