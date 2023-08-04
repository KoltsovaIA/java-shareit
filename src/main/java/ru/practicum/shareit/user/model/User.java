package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @NotBlank(message = "При создании нового пользователя необходимо указать его e-mail.")
    @Email(message = "При создании нового пользователя необходимо указать кооректный e-mail.")
    @Column(name = "user_email", length = 100, nullable = false, unique = true)
    private String email;
    @NotBlank(message = "При создании нового пользователя необходимо указать его имя")
    @Column(name = "user_name", length = 100, nullable = false)
    private String name;
}