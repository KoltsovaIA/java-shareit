package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private static UserMapper userMapper;
    private static User user;
    private static UserDto userDto;

    @BeforeEach
    void beforeAll() {
        UserService userService = Mockito.mock(UserService.class);
        userMapper = new UserMapper(userService);
        user = new User(1L, "user@email.ru", "User1");
        userDto = new UserDto(1L, "user@email.ru", "User1");
    }

    @Test
    void dtoToUser() {
        assertEquals(user, userMapper.dtoToUser(userDto));
    }

    @Test
    void userToDto() {
        assertEquals(userDto, userMapper.userToDto(user));
    }

    @Test
    void listUserToListDto() {
        List<UserDto> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        List<User> users = new ArrayList<>();
        users.add(user);
        assertEquals(usersDto, userMapper.listUserToListDto(users));
    }
}