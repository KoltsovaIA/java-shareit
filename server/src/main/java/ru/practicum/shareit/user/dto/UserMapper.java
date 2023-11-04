package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class UserMapper {

    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> listUserToListDto(List<User> users) {
        List<UserDto> listUserDto = new ArrayList<>();
        users.forEach(value -> listUserDto.add(userToDto(value)));
        return listUserDto;
    }
}