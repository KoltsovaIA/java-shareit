package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requestDto.IncomingItemRequestDto;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid IncomingItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.findOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE)
                                                      int from,
                                                      @RequestParam(defaultValue = "32") @Min(1) @Max(32) int size) {
        return itemRequestClient.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable long requestId) {
        return itemRequestClient.findItemRequestById(userId, requestId);
    }
}