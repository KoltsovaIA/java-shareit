package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.LinkedList;
import java.util.List;

public class RequestMapper {
    private RequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static OutgoingItemRequestDto itemRequestToOutgoingItemRequestDto(ItemRequest request, List<Item> items) {
        List<ShortItemDto> shortItemsDto = new LinkedList<>();
        items.forEach(value -> shortItemsDto.add(itemToShortItemDto(value)));
        return OutgoingItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(shortItemsDto)
                .build();
    }

    public static ShortItemDto itemToShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }
}