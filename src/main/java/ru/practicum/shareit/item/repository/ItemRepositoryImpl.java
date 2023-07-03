package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    @Autowired
    private UserService userService;
    private static int id = 0;

    @Override
    public Item createItem(@Valid Item item) {
        userService.getUserById(item.getOwner().getId());
        item.setId(getNewId());
        items.put(item.getId(), Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build()
        );
        log.info("Создана вещь: " + item.getName());
        return item;
    }

    @Override
    public Item getItemById(int id) {
        itemIdIsExist(id);
        Item item = items.get(id);
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    @Override
    public Item updateItemById(@Valid Item item) {
        Integer itemId = item.getId();
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена.");
        }
        Item oldItem = getItemById(itemId);
        if (!Objects.equals(item.getOwner(), oldItem.getOwner())) {
            throw new ItemNotFoundException("Обновить вещь с id " + itemId + " может только пользователь с id " +
                    oldItem.getOwner().getId());
        }
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        if (item.getRequest() == null) {
            item.setRequest(oldItem.getRequest());
        }
        userService.getUserById(item.getOwner().getId());
        items.replace(itemId, item);
        log.info("Обновлена вещь " + item.getName());
        return item;
    }

    @Override
    public void deleteItemById(int id) {
        itemIdIsExist(id);
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Вещь с id " + " не найдена.");
        }
        items.remove(id);
        log.info("Удалена вещь с id " + id);
    }

    @Override
    public List<Item> findAllItemsByUserId(Integer userId) {
        ArrayList<Item> itemsList = new ArrayList<>();
        for (Item value : items.values()) {
            if (Objects.equals(value.getOwner().getId(), userId)) {
                itemsList.add(Item.builder()
                        .id(value.getId())
                        .name(value.getName())
                        .description(value.getDescription())
                        .available(value.getAvailable())
                        .owner(value.getOwner())
                        .request(value.getRequest())
                        .build());
            }
        }
        return itemsList;
    }

    @Override
    public List<Item> searchItem(String text) {
        ArrayList<Item> itemsList = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            for (Item value : items.values()) {
                if ((StringUtils.containsIgnoreCase(value.getDescription(), text) ||
                        StringUtils.containsIgnoreCase(value.getName(), text)) &&
                        Boolean.TRUE.equals(value.getAvailable())) {
                    itemsList.add(value);
                }
            }
        }
        log.info("Найдены вещи, содержащие текст " + text + " в описании или названии.");
        return itemsList;
    }

    public static int getNewId() {
        return ++id;
    }

    public void itemIdIsExist(int id) {
        if ((id < 0) || (!items.containsKey(id))) {
            log.error("Передан некорректный id " + id);
            throw new ItemNotFoundException("Некорректный id " + id);
        }
    }
}