package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private static final String USER_WITH_ID = "Пользователь с id ";
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();


    @Override
    public User createUser(@Valid User user) {
        users.forEach((key, value) -> {
            if (value.getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        });
        user.setId(getNewId());
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build()
        );
        log.info("Добавлен пользователь " + user.getName() + users.size());
        return user;
    }

    @Override
    public User getUserById(int id) {
        userIdIsExist(id);
        User user = users.get(id);
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Override
    public User updateUserById(@Valid User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(USER_WITH_ID + id + " не найден.");
        }
        User oldUser = getUserById(user.getId());
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        users.forEach((key, value) -> {
            if (value.getEmail().equals(user.getEmail()) && !value.getEmail().equals(oldUser.getEmail())) {
                throw new EmailAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        });
        users.replace(user.getId(), user);
        log.info("Обновлен пользователь " + user.getName());
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        userIdIsExist(id);
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(USER_WITH_ID + id + " не найден!");
        }
        users.remove(id);
        log.info(USER_WITH_ID + id + " удален.");
    }

    @Override
    public List<User> findAllUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        for (User value : users.values()) {
            usersList.add(User.builder()
                    .id(value.getId())
                    .email(value.getEmail())
                    .name(value.getName())
                    .build());
        }
        log.info("Сформирован список всех пользователей.");
        return usersList;
    }

    public static int getNewId() {
        return ++id;
    }

    public void userIdIsExist(int id) {
        if ((id < 0) || (!users.containsKey(id))) {
            log.error("Передан некорректный id " + id);
            throw new UserNotFoundException("Некорректный id " + id);
        }
    }
}