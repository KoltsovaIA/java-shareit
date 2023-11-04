package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestBody IncomingItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long id,
                                  @RequestBody IncomingItemDto itemDto) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @PathVariable Long id) {
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long owner) {
        return itemService.getAllByOwner(owner);
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@RequestParam(required = false, name = "text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping({"/{itemId}/comment"})
    public OutgoingCommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @PathVariable(name = "itemId") long itemId,
                                            @RequestBody IncomingCommentDto incomingCommentDto) {
        return itemService.createComment(userId, itemId, incomingCommentDto);
    }
}