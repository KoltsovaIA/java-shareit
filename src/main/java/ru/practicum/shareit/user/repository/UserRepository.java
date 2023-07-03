package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUserById(int id);

    User updateUserById(User user);

    void deleteUserById(int id);

    List<User> findAllUsers();
}