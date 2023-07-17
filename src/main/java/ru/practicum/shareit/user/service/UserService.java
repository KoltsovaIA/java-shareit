package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUserById(Long id);

    List<User> findAllUsers();

    boolean userIsExistsById(Long id);
}