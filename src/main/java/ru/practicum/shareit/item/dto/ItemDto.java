package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<OutgoingCommentDto> comments;
    private Long requestId;
}