package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userService.getUserById(userId));
        return itemToDto(itemService.createItem(dtoToItem(itemDto)));
    }

    @PatchMapping({"/{id}"})
    public ItemDto updateItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id,
                                  @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        itemDto.setOwner(userService.getUserById(userId));
        return itemToDto(itemService.updateItemById(dtoToItem(itemDto)));
    }

    @GetMapping({"/{id}"})
    public ItemDto getItemById(@PathVariable Integer id) {
        return itemToDto(itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> findAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return listItemToListDto(itemService.findAllItemsByUserId(userId));
    }

    @GetMapping({"/search"})
    public List<ItemDto> searchItem(@RequestParam(required = false, name = "text") String text) {
        return listItemToListDto(itemService.searchItem(text));
    }
}