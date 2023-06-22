package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(int id);

    User updateUserById(User user);

    void deleteUserById(int id);

    List<User> findAllUsers();
}