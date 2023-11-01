package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.userDto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private static User user;
    private static UserDto userDto;

    @BeforeEach
    void beforeAll() {
        user = new User(1L, "user@email.ru", "User1");
        userDto = new UserDto(1L, "user@email.ru", "User1");
    }

    @Test
    void userToDto() {
        assertThat(UserMapper.userToDto(user))
                .hasFieldOrPropertyWithValue("id", userDto.getId())
                .hasFieldOrPropertyWithValue("email", userDto.getEmail())
                .hasFieldOrPropertyWithValue("name", userDto.getName());
    }

    @Test
    void listUserToListDto() {
        List<User> users = new ArrayList<>();
        users.add(user);
        assertThat(UserMapper.listUserToListDto(users))
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", userDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", userDto.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", userDto.getEmail());
                });
    }
}