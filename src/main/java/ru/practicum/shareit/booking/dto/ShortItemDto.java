package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortItemDto {
    private Long id;
    private String name;
}