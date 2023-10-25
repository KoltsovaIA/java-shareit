package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class OutgoingCommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}