package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.userDto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        return UserMapper.userToDto(userService.createUser(userDto));
    }

    @PatchMapping({"/{id}"})
    public UserDto updateUserById(@PathVariable Long id, @RequestBody @Validated(OnUpdate.class) UserDto userDto) {
        userDto.setId(id);
        return UserMapper.userToDto(userService.updateUser(userDto));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return UserMapper.listUserToListDto(userService.findAllUsers());
    }

    @GetMapping({"/{id}"})
    public UserDto getUserById(@Valid @PathVariable Long id) {
        return UserMapper.userToDto(userService.getUserById(id));
    }

    @DeleteMapping({"/{id}"})
    public void deleteUserById(@Valid @PathVariable Long id) {
        userService.deleteUserById(id);
    }
}