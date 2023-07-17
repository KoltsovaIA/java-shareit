package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomingCommentDto {
    private Long id;
    @NotBlank(message = "Комментарий не может быть пустым.")
    private String text;
    private Long itemId;
    private Long authorId;
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime created;
}