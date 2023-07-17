package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название вещи должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть добавлено")
    private String description;
    @NotNull(message = "Статус бронирования должен быть указан")
    private Boolean available;
    private long owner;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<OutgoingCommentDto> comments;
}