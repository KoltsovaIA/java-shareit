package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class UserServiceTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);

    @InjectMocks
    UserService userService = new UserServiceImpl(userRepository);

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user@email.ru", "User1");
    }

    @Test
    void createUserTest() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);
        Mockito.when(userRepository.findAll())
                .thenReturn(java.util.List.of(user));
        User createdUser = userService.createUser(user);
        assertEquals(createdUser, user);
        assertEquals(1, userService.findAllUsers().size());
        assertEquals(createdUser.getName(), user.getName());
        assertEquals(createdUser.getEmail(), user.getEmail());
    }

    @Test
    void getUserByIdTest() {
         userService.createUser(user);
        Mockito.when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);

        assertEquals(userService.getUserById(1L), user);
        User userById = userService.getUserById(1L);
        assertEquals(userById.getId(), user.getId());
        assertEquals(userById.getName(), user.getName());
        assertEquals(userById.getEmail(), user.getEmail());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L),
                "Метод create работает некорректно при попытке получить пользователя с несуществующим id");
    }

    @Test
    void updateUserTest() {
        Mockito.when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);
        Mockito.when(userRepository.findAll())
                .thenReturn(java.util.List.of(user));
        user.setEmail("NewUser");
        userService.updateUser(user);
        User testUser = userService.getUserById(1L);
        assertEquals(user, testUser, "Метод update работает некорректно. Пользователи не совпадают");
        assertEquals(1, userService.findAllUsers().size(),
                "Метод update работает некорректно. Неверное число пользователей");
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userService.getUserById(8888L)),
                "Метод update работает некорректно при запросе пользователя с некорректным id ");

    }

    @Test
    void findAllUsersTest(){
        Mockito.when(userRepository.findAll())
                .thenReturn(java.util.List.of(user));
        assertEquals(userService.findAllUsers(), java.util.List.of(user));
    }

    @Test
    void userIsExistsByIdTest() {
        Mockito.when(userRepository.existsById(user.getId()))
                .thenReturn(true);
        assertTrue(userService.userIsExistsById(1L));
        assertFalse(userService.userIsExistsById(2L));
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUserById(1L);
    }
}