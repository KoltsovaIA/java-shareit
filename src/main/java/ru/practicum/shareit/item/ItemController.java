package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Integer userId, @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userService.getUserById(userId));
        return itemToDto(itemService.createItem(dtoToItem(itemDto)));
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@Valid @RequestHeader(USER_ID_HEADER) Integer userId, @PathVariable Integer id,
                                  @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        itemDto.setOwner(userService.getUserById(userId));
        return itemToDto(itemService.updateItemById(dtoToItem(itemDto)));
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@Valid @PathVariable Integer id) {
        return itemToDto(itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> findAllItemsByUserId(@Valid @RequestHeader(USER_ID_HEADER) Integer userId) {
        return listItemToListDto(itemService.findAllItemsByUserId(userId));
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@Valid @RequestParam(required = false, name = "text") String text) {
        return listItemToListDto(itemService.searchItem(text));
    }
}