package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.userDto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) {
        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build());
    }

    @Override
    public User getUserById(Long id) {
        if (!userIsExistsById(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return userRepository.getReferenceById(id);
    }

    @Override
    public User updateUser(UserDto userDto) {
        if (!Objects.equals(userRepository.getReferenceById(userDto.getId()).getEmail(), userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistException("электронная почта " + userDto.getEmail() + " уже зарегистрирована");
        }
        return userRepository.save(User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail() != null ? userDto.getEmail() : getUserById(userDto.getId()).getEmail())
                .name(userDto.getName() != null ? userDto.getName() : getUserById(userDto.getId()).getName())
                .build());
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean userIsExistsById(Long id) {
        return userRepository.existsById(id);
    }
}