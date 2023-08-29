package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;
    private final RequestMapper requestMapper;

    @PostMapping
    public OutgoingItemRequestDto createItemRequest(@Valid @RequestBody IncomingItemRequestDto dto,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return requestMapper.itemRequestToOutgoingItemRequestDto(
                itemRequestService.createItemRequest(requestMapper
                        .incomingItemRequestDtoToItemRequest(userId, dto)));
    }

    @GetMapping
    public List<OutgoingItemRequestDto> findOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return requestMapper.listRequestToListDto(itemRequestService.getAllItemRequestByRequester(userId));
    }

    @GetMapping("/all")
    public List<OutgoingItemRequestDto> findAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                            @RequestParam(defaultValue = "0", required = false)
                                                            @PositiveOrZero Short from,
                                                            @RequestParam(defaultValue = "32", required = false)
                                                            @Positive Short size) {
        return requestMapper.listRequestToListDto(itemRequestService.getAllItemRequest(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public OutgoingItemRequestDto findItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable long requestId) {
        return requestMapper.itemRequestToOutgoingItemRequestDto(itemRequestService
                .findItemRequestsById(userId, requestId));
    }
}