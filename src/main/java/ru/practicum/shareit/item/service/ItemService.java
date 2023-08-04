package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long id);

    List<Item> getAllByOwner(long owner);

    boolean itemIsExistsById(Long id);

    boolean itemIsAvailableById(Long id);

    List<Item> searchItems(String text);

    Comment createComment(Comment comment);

    List<Comment> findAllCommentsByItemId(Long itemId);

    List<Item> getAllByItemRequestId(Long requestId);
}