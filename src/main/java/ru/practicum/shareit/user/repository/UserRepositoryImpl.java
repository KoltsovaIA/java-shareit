package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User createUser(User user) {
        checkUser(user);
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
    public User updateUserById(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        User oldUser = getUserById(user.getId());
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        checkUser(user);
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
            throw new UserNotFoundException("Пользователь с id " + id + " не найден!");
        }
        users.remove(id);
        log.info("Пользователь с id " + id + " удален.");
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

    public int getNewId() {
        return ++id;
    }

    public int getLastId() {
        return id;
    }

    private void checkUser(User user) {
        if (StringUtils.isBlank(user.getEmail())) {
            throw new IncorrectParameterException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new IncorrectParameterException("Некорректный адрес электронной почты");
        }
        if (StringUtils.isBlank(user.getName())) {
            throw new IncorrectParameterException("Имя пользователя не может быть пустым.");
        }
    }

    public void userIdIsExist(int id) {
        if ((id < 0) || (!users.containsKey(id))) {
            log.error("Передан некорректный id " + id);
            throw new UserNotFoundException("Некорректный id " + id);
        }
    }
}