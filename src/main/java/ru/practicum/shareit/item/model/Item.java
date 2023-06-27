package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    private Integer id;
    @NotBlank(message = "При создании новой вещи необходимо передать её название.")
    private String name;
    @NotBlank(message = "При создании новой вещи необходимо передать её описание.")
    private String description;
    @NotNull(message = "При создании новой вещи необходимо указать статус бронирования.")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}