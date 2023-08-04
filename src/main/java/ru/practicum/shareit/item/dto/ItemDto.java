package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
class ShortBooking {
    Long id;
    Long bookerId;
}

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
    private Long owner;
    private ShortBooking lastBooking;
    private ShortBooking nextBooking;
    private List<OutgoingCommentDto> comments;
    private Long requestId;
}

