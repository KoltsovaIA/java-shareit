package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static UserRepository userRepository;
    private static UserService userService;
    private static User user;

    @BeforeEach
    void beforeAll() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(1L, "user@email.ru", "User1");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User createdUser = userService.createUser(user);
        assertEquals(user, createdUser);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(userRepository.existsById(1L))
                .thenReturn(true);
        assertEquals(user, userService.getUserById(1L));
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L),
                "Метод create работает некорректно при попытке получить пользователя с несуществующим id");
    }

    @Test
    void updateUserTest() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        when(userRepository.existsByEmail("updatetUser@email.ru"))
                .thenReturn(true);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        assertEquals(user, userService.updateUser(user));
        User updatetUser = new User(1L, "updatetUser@email.ru", "User1");
        assertThrows(EmailAlreadyExistException.class, () -> userService.updateUser(updatetUser));
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(java.util.List.of(user));
        assertEquals(userService.findAllUsers(), java.util.List.of(user));
    }

    @Test
    void userIsExistsByIdTest() {
        when(userRepository.existsById(user.getId()))
                .thenReturn(true);
        assertTrue(userService.userIsExistsById(1L));
        assertFalse(userService.userIsExistsById(2L));
    }

    @Test
    void deleteUserByIdTest() {
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}