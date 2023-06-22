package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service

public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item item) {
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItemById(Item item) {
        return itemRepository.updateItemById(item);
    }

    @Override
    public Item getItemById(int id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public List<Item> findAllItemsByUserId(Integer userId) {
        return itemRepository.findAllItemsByUserId(userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemRepository.searchItem(text);
    }
}