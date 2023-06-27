package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(message = "При создании новой вещи необходимо указать её название.")
    private String name;
    @NotBlank(message = "При создании новой вещи необходимо добавить её описание.")
    private String description;
    @NotNull(message = "При создании новой вещи необходимо указать статус бронирования.")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}