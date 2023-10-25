package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ShortItemDto;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class OutgoingItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ShortItemDto> items;
}