package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    private Integer id;
    @NotBlank(message = "При создании нового пользователя необходимо указать его e-mail.")
    @Email(message = "При создании нового пользователя необходимо указать кооректный e-mail.")
    private String email;
    @NotBlank(message = "При создании нового пользователя необходимо указать его имя")
    private String name;
}