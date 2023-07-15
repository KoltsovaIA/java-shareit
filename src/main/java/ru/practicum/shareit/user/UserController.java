package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userMapper.userToDto(userService.createUser(userMapper.dtoToUser(userDto)));
    }

    @PatchMapping({"/{id}"})
    public UserDto updateUserById(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userMapper.userToDto(userService.updateUser(userMapper.dtoToUser(userDto)));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userMapper.listUserToListDto(userService.findAllUsers());
    }

    @GetMapping({"/{id}"})
    public UserDto getUserById(@Valid @PathVariable Long id) {
        return userMapper.userToDto(userService.getUserById(id));
    }

    @DeleteMapping({"/{id}"})
    public void deleteUserById(@Valid @PathVariable Long id) {
        userService.deleteUserById(id);
    }
}