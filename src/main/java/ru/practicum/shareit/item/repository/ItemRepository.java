package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item getItemById(int id);

    Item updateItemById(Item item);

    void deleteItemById(int id);

    List<Item> findAllItemsByUserId(Integer userId);

    List<Item> searchItem(String text);
}