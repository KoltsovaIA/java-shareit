package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requestDto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public OutgoingItemRequestDto createItemRequest(@Valid @RequestBody IncomingItemRequestDto dto,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.createItemRequest(userId, dto);
    }

    @GetMapping
    public List<OutgoingItemRequestDto> findOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getAllItemRequestByRequester(userId);
    }

    @GetMapping("/all")
    public List<OutgoingItemRequestDto> findAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                            @RequestParam(defaultValue = "0")
                                                            @PositiveOrZero Short from,
                                                            @RequestParam(defaultValue = "32")
                                                            @Positive Short size) {
        return itemRequestService.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public OutgoingItemRequestDto findItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable long requestId) {
        return itemRequestService.findItemRequestsById(userId, requestId);
    }
}