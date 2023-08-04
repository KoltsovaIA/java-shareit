package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class RequestMapper {
    private final ItemService itemService;
    private final UserService userService;

    public ItemRequest incomingItemRequestDtoToItemRequest(Long requesterId, IncomingItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(null)
                .requester(userService.getUserById(requesterId))
                .created(LocalDateTime.now())
                .description(requestDto.getDescription())
                .build();
    }

    public OutgoingItemRequestDto itemRequestToOutgoingItemRequestDto(ItemRequest request) {
        List<ShortItemDto> shortItemsDto = new LinkedList<>();
        List<Item> items = itemService.getAllByItemRequestId(request.getId());
        items.forEach(value -> shortItemsDto.add(itemToShortItemDto(value)));
        return OutgoingItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(shortItemsDto)
                .build();
    }

    public List<OutgoingItemRequestDto> listRequestToListDto(List<ItemRequest> itemRequests) {
        LinkedList<OutgoingItemRequestDto> listItemRequestDto = new LinkedList<>();
        itemRequests.forEach(value -> listItemRequestDto.add(itemRequestToOutgoingItemRequestDto(value)));
        return listItemRequestDto;
    }

    public ShortItemDto itemToShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }
}
