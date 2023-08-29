package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequest itemRequest);

    List<ItemRequest> getAllItemRequestByRequester(Long requesterId);

    List<ItemRequest> getAllItemRequest(Long requesterId, Short from, Short size);

    ItemRequest findItemRequestsById(Long userId, Long requestId);
}