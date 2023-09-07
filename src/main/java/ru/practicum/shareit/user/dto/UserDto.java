package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "При создании нового пользователя необходимо указать его e-mail.")
    @Email(message = "При создании нового пользователя необходимо указать кооректный e-mail.")
    private String email;
    @NotBlank(message = "При создании нового пользователя необходимо указать его имя")
    private String name;
}