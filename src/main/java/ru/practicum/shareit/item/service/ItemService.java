package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItemById(Item item);

    Item getItemById(int id);

    List<Item> findAllItemsByUserId(Integer userId);

    List<Item> searchItem(String text);
}