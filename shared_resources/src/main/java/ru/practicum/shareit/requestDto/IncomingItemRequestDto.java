package ru.practicum.shareit.requestDto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IncomingItemRequestDto {
    @Size(max = 512)
    @NotBlank(message = "При создании нового запроса вещей, поле описания не может быть пустым")
    private String description;
}