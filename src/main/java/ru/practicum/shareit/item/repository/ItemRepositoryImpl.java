package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Slf4j
@Service
public class ItemRepositoryImpl implements ItemRepository {
    @Autowired
    private UserService userService;
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public Item createItem(Item item) {
        checkItem(item);
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
    public Item updateItemById(Item item) {
        Integer itemId = item.getId();
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена.");
        }
        Item oldItem = getItemById(itemId);
        if (!Objects.equals(item.getOwner(), oldItem.getOwner())) {
            throw new ItemNotFoundException("Обновить вещь может только её владелец.");
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
        checkItem(item);
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
        if (!StringUtils.isBlank(text)) {
            for (Item value : items.values()) {
                if ((value.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        value.getName().toLowerCase().contains(text.toLowerCase())) && value.getAvailable()) {
                    itemsList.add(value);
                }
            }
        }
        log.info("Найдены вещи, содержащие текст " + text + " в описании или названии.");
        return itemsList;
    }

    private void checkItem(Item item) {
        if (item.getOwner() == null) {
            throw new IncorrectParameterException("Владелец вещи не может быть не указан.");
        }
        userService.getUserById(item.getOwner().getId());
        if (item.getAvailable() == null) {
            throw new IncorrectParameterException("Статус доступности вещи не может быть не указан.");
        }
        if (StringUtils.isBlank(item.getName())) {
            throw new IncorrectParameterException("Название вещи не может быть не указано.");
        }
        if (StringUtils.isBlank(item.getDescription())) {
            throw new IncorrectParameterException("Описание вещи не может быть не заполнено.");
        }
    }

    public int getNewId() {
        return ++id;
    }

    public int getLastId() {
        return id;
    }

    public void itemIdIsExist(int id) {
        if ((id < 0) || (!items.containsKey(id))) {
            log.error("Передан некорректный id " + id);
            throw new ItemNotFoundException("Некорректный id " + id);
        }
    }
}