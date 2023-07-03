package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userToDto(userService.createUser(dtoToUser(userDto)));
    }

    @PatchMapping({"/{id}"})
    public UserDto updateUserById(@Valid @PathVariable Integer id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userToDto(userService.updateUserById(dtoToUser(userDto)));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return listUserToListDto(userService.findAllUsers());
    }

    @GetMapping({"/{id}"})
    public UserDto getUserById(@Valid @PathVariable Integer id) {
        return userToDto(userService.getUserById(id));
    }

    @DeleteMapping({"/{id}"})
    public void deleteUserById(@Valid @PathVariable Integer id) {
        userService.deleteUserById(id);
    }
}