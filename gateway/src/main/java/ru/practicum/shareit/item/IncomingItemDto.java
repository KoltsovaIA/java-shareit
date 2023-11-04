package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IncomingItemDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Название вещи должно быть указано")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 100)
    private String name;
    @NotBlank(groups = OnCreate.class, message = "Описание должно быть добавлено")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 200)
    private String description;
    @NotNull(groups = OnCreate.class, message = "Статус бронирования должен быть указан")
    private Boolean available;
    private Long requestId;
}
