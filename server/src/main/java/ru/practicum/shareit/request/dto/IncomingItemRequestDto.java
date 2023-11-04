package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IncomingItemRequestDto {
    private String description;
}