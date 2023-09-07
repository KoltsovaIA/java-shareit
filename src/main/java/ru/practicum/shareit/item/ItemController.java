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

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userId);
        return itemMapper.itemToDto(userId, itemService.createItem(itemMapper.dtoToItem(itemDto)));
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @NotNull @PathVariable Long id,
                                  @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        itemDto.setOwner(userId);
        return itemMapper.itemToDto(userId, itemService.updateItem(itemMapper.dtoToItem(itemDto)));
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @NotNull @PathVariable Long id) {
        return itemMapper.itemToDto(userId, itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long owner) {
        return itemMapper.listItemToListDto(itemService.getAllByOwner(owner));
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@NotBlank @RequestParam(required = false, name = "text") String text) {
        return itemMapper.listItemToListDto(itemService.searchItems(text));
    }

    @PostMapping({"/{itemId}/comment"})
    public OutgoingCommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @NotNull @PathVariable(name = "itemId") long itemId,
                                            @Valid @RequestBody IncomingCommentDto incomingCommentDto) {
        incomingCommentDto.setAuthorId(userId);
        incomingCommentDto.setItemId(itemId);
        incomingCommentDto.setCreated(LocalDateTime.now());
        return itemMapper.commentToDto(itemService.createComment(itemMapper.dtoToComment(incomingCommentDto)));
    }
}