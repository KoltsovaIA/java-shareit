package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ShortItemDto;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingItemRequestDto {
    @NotNull(message = "ID запроса должно быть указано")
    @Min(1)
    private Long id;

    @Size(max = 512)
    @NotBlank(message = "При создании нового запроса вещей, поле описания не может быть пустым")
    private String description;

    @NotNull(message = "Время создания запроса должно быть указано")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Temporal(TemporalType.DATE)
    private LocalDateTime created;

    private List<ShortItemDto> items;
}