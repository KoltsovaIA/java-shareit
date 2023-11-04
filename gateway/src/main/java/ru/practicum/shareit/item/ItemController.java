package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody @Validated(OnCreate.class) IncomingItemDto itemDto) {
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long itemId,
                                                 @RequestBody @Validated(OnUpdate.class) IncomingItemDto itemDto) {
        return client.updateItemById(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                              @PathVariable long itemId) {
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE)
                                                 int from,
                                                 @RequestParam(defaultValue = "32") @Min(1) @Max(32) int size) {
        return client.findAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int from,
                                             @RequestParam(defaultValue = "32") @Min(1) @Max(32) int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return client.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid IncomingCommentDto commentDto) {
        return client.createComment(userId, itemId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        client.deleteItem(userId, itemId);
    }
}
