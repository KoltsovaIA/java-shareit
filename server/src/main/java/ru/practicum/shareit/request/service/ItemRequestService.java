package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    OutgoingItemRequestDto createItemRequest(Long userId, IncomingItemRequestDto itemRequestDto);

    List<OutgoingItemRequestDto> getAllItemRequestByRequester(Long requesterId);

    List<OutgoingItemRequestDto> getAllItemRequest(Long requesterId, Short from, Short size);

    OutgoingItemRequestDto findItemRequestsById(Long userId, Long requestId);
}