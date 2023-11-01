package ru.practicum.shareit.ItemTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.itemDto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.userDto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;
    private static IncomingItemDto itemDto;
    private static Item item;
    private static IncomingItemDto itemDto2;
    private static Item item2;
    private static UserDto userDto;
    private static User user;

    @BeforeAll
    static void beforeAll() {
        userDto = UserDto.builder()
                .name("name")
                .email("user@email.ru")
                .build();

        user = User.builder()
                .id(1L)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        itemDto = IncomingItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        item = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .itemRequest(null)
                .build();

        itemDto2 = IncomingItemDto.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name(itemDto2.getName())
                .description(itemDto2.getDescription())
                .available(itemDto2.getAvailable())
                .owner(user)
                .itemRequest(null)
                .build();
    }

    @Test
    void createItemTest() {
        userService.createUser(userDto);
        itemService.createItem(user.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", user.getId()).getSingleResult();
        assertThat(itemDto.getName(), equalTo(itemFromDb.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemFromDb.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemFromDb.getAvailable()));
        assertNull(itemFromDb.getItemRequest());
    }

    @Test
    void updateItemTest() {
        userService.createUser(userDto);
        itemService.createItem(user.getId(), itemDto);
        itemDto.setName("newName");
        itemService.updateItem(user.getId(), item.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", user.getId()).getSingleResult();
        assertThat(itemDto.getName(), equalTo(itemFromDb.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemFromDb.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemFromDb.getAvailable()));
        assertNull(itemFromDb.getItemRequest());
    }

    @Test
    void getItemByIdTest() {
        userService.createUser(userDto);
        itemService.createItem(user.getId(), itemDto);
        ItemDto outgoingItemDto = itemService.getItemById(user.getId(), item.getId());
        assertThat(itemDto.getName(), equalTo(outgoingItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(outgoingItemDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(outgoingItemDto.getAvailable()));
        assertNull(outgoingItemDto.getRequestId());
    }

    @Test
    void getAllByOwnerTest() {
        userService.createUser(userDto);
        itemService.createItem(user.getId(), itemDto);
        itemService.createItem(user.getId(), itemDto2);
        List<ItemDto> items = itemService.getAllByOwner(user.getId());
        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", item.getName());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", item2.getName());
                });
    }

    @Test
    void searchItemsTest() {
        userService.createUser(userDto);
        itemService.createItem(user.getId(), itemDto);
        itemService.createItem(user.getId(), itemDto2);
        List<ItemDto> items = itemService.searchItems("name2");
        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", item2.getName());
                });
    }
}