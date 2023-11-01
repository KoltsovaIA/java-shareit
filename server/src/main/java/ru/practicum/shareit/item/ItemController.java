package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemDto.IncomingCommentDto;
import ru.practicum.shareit.itemDto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestBody @Validated(OnCreate.class) IncomingItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @NotNull @PathVariable Long id,
                                  @RequestBody @Validated(OnUpdate.class) IncomingItemDto itemDto) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @NotNull @PathVariable Long id) {
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long owner) {
        return itemService.getAllByOwner(owner);
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@NotBlank @RequestParam(required = false, name = "text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping({"/{itemId}/comment"})
    public OutgoingCommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @NotNull @PathVariable(name = "itemId") long itemId,
                                            @Valid @RequestBody IncomingCommentDto incomingCommentDto) {
        return itemService.createComment(userId, itemId, incomingCommentDto);
    }
}