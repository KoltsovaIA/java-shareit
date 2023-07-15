package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
//        if (userRepository.existsByEmail(user.getEmail())){
//            throw new EmailAlreadyExistException("электронная почта " + user.getEmail() + " уже зарегистрирована");
//        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return userRepository.getReferenceById(id);
    }

    @Override
    public User updateUser(User user) {
        if (!Objects.equals(userRepository.getReferenceById(user.getId()).getEmail(), user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistException("электронная почта " + user.getEmail() + " уже зарегистрирована");
        }
        return userRepository.save(user);
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
    public boolean userIsExistsById(Long id){
        return userRepository.existsById(id);
    }
}