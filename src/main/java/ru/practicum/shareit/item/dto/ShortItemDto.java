package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ShortItemDto {
    private Long id;
    @NotBlank(message = "Название вещи должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть добавлено")
    private String description;
    @NotNull(message = "Статус бронирования должен быть указан")
    private Boolean available;
    private Long requestId;
}
