package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "При создании нового пользователя необходимо указать его e-mail.")
    @Email(groups = {OnCreate.class, OnUpdate.class},
            message = "При создании или обновлении пользователя необходимо указать кооректный e-mail.")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 100)
    private String email;
    @NotBlank(groups = OnCreate.class, message = "При создании нового пользователя необходимо указать его имя")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 100)
    private String name;
}