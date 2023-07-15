package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
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