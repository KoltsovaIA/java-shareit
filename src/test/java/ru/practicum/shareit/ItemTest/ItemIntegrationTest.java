package ru.practicum.shareit.ItemTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
    private static Item item;
    private static Item item2;
    private static User user;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .itemRequest(null)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user)
                .itemRequest(null)
                .build();
    }

    @Test
    void createItemTest() {
        userService.createUser(user);
        itemService.createItem(item);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(item.getName(), equalTo(itemFromDb.getName()));
        assertThat(item.getDescription(), equalTo(itemFromDb.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemFromDb.getAvailable()));
        assertNull(item.getItemRequest());
    }

    @Test
    void updateItemTest() {
        userService.createUser(user);
        itemService.createItem(item);
        item.setName("newName");
        itemService.updateItem(item);
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(item.getName(), equalTo(itemFromDb.getName()));
        assertThat(item.getDescription(), equalTo(itemFromDb.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemFromDb.getAvailable()));
        assertNull(itemFromDb.getItemRequest());
    }

    @Test
    void getItemByIdTest() {
        userService.createUser(user);
        itemService.createItem(item);
        Item itemFromDb = itemService.getItemById(1L);
        assertThat(item.getName(), equalTo(itemFromDb.getName()));
        assertThat(item.getDescription(), equalTo(itemFromDb.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemFromDb.getAvailable()));
        assertNull(item.getItemRequest());
    }

    @Test
    void getAllByOwnerTest() {
        userService.createUser(user);
        itemService.createItem(item);
        itemService.createItem(item2);
        List<Item> items = itemService.getAllByOwner(1L);
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
        userService.createUser(user);
        itemService.createItem(item);
        itemService.createItem(item2);
        List<Item> items = itemService.searchItems("name2");
        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", item2.getName());
                });
    }
}