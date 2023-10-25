package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, IncomingItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, IncomingItemDto itemDto);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> getAllByOwner(long owner);

    boolean itemIsExistsById(Long id);

    boolean itemIsAvailableById(Long id);

    List<ItemDto> searchItems(String text);

    OutgoingCommentDto createComment(Long authorId, Long itemId, IncomingCommentDto commentDto);

    List<OutgoingCommentDto> findAllCommentsByItemId(Long itemId);

    List<ItemDto> getAllByItemRequestId(Long requestId);
}