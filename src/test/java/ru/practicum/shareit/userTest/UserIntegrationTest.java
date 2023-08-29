package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTest {
    private final UserService userService;
    private final EntityManager entityManager;

    private static User user;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .name("name")
                .email("email@ya.ru")
                .build();
    }

    @Test
    void createUserTest() {
        userService.createUser(user);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(user.getName(), equalTo(userFromDb.getName()));
        assertThat(user.getEmail(), equalTo(userFromDb.getEmail()));
    }

    @Test
    void updateUserTest() {
        userService.createUser(user);
        user.setName("newName");
        userService.updateUser(user);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User updatedUser = query.setParameter("id", 1L).getSingleResult();
        assertThat(user.getName(), equalTo(updatedUser.getName()));
    }

    @Test
    void deleteUserByIdTest() {
        userService.createUser(user);
        assertThat(userService.findAllUsers().size(), equalTo(1));
        userService.deleteUserById(1L);
        assertThat(userService.findAllUsers().size(), equalTo(0));
    }

    @Test
    void getUserById() {
        userService.createUser(user);
        User userFromDb = userService.getUserById(1L);
        assertThat(user.getName(), equalTo(userFromDb.getName()));
        assertThat(user.getEmail(), equalTo(userFromDb.getEmail()));
    }

    @Test
    void shouldGetAll() {
        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setName("user2");
        User user3 = new User();
        user3.setEmail("user3@mail.ru");
        user3.setName("user3");
        userService.createUser(user);
        userService.createUser(user2);
        userService.createUser(user3);
        List<User> users = userService.findAllUsers();
        assertThat(users)
                .isNotEmpty()
                .hasSize(3)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", user.getEmail());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("email", user2.getEmail());
                    assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(list.get(2)).hasFieldOrPropertyWithValue("email", user3.getEmail());
                });
    }
}