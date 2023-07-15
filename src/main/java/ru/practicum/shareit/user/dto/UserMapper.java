package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserMapper {
    private final UserService userService;

    public User dtoToUser(UserDto userDto) {
        Long id = userDto.getId();
        return User.builder()
                .id(id)
                .email(userDto.getEmail() != null ? userDto.getEmail() : userService.getUserById(id).getEmail() )
                .name(userDto.getName() != null ? userDto.getName() : userService.getUserById(id).getName())
                .build();
    }

    public UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public List<UserDto> listUserToListDto(List<User> users) {
        List<UserDto> listUserDto = new ArrayList<>();
        users.forEach (value -> {listUserDto.add(userToDto(value));});
        return listUserDto;
    }
}