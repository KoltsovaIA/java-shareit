package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userId);
        return itemMapper.itemToDto(userId, itemService.createItem(itemMapper.dtoToItem(itemDto)));
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                  @NotNull @PathVariable Long id,
                                  @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        itemDto.setOwner(userId);
        return itemMapper.itemToDto(userId, itemService.updateItem(itemMapper.dtoToItem(itemDto)));
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                               @NotNull @PathVariable Long id) {
        return itemMapper.itemToDto(userId, itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> findAllByOwner(@NotNull @RequestHeader(USER_ID_HEADER) Long owner) {
        return itemMapper.listItemToListDto(itemService.getAllByOwner(owner));
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@NotBlank @RequestParam(required = false, name = "text") String text) {
        return itemMapper.listItemToListDto(itemService.searchItems(text));
    }

    @PostMapping({"/{itemId}/comment"})
    public OutgoingCommentDto createComment(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                            @NotNull @PathVariable(name = "itemId") long itemId,
                                            @Valid @RequestBody IncomingCommentDto incomingCommentDto) {
        incomingCommentDto.setAuthorId(userId);
        incomingCommentDto.setItemId(itemId);
        incomingCommentDto.setCreated(LocalDateTime.now());
        return itemMapper.commentToDto(itemService.createComment(itemMapper.dtoToComment(incomingCommentDto)));
    }
}