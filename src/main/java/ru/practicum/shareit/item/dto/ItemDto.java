package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название вещи должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть добавлено")
    private String description;
    @NotNull(message = "Статус бронирования должен быть указан")
    private Boolean available;
    private Long owner;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<OutgoingCommentDto> comments;
    private Long requestId;
}