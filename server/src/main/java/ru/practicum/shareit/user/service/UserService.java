package ru.practicum.shareit.user.service;

import ru.practicum.shareit.userDto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto user);

    User getUserById(Long id);

    User updateUser(UserDto user);

    void deleteUserById(Long id);

    List<User> findAllUsers();

    boolean userIsExistsById(Long id);
}