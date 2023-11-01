package ru.practicum.shareit.itemDto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class IncomingCommentDto {
    @NotBlank(message = "Комментарий не может быть пустым.")
    private String text;
}