package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userToDto(userService.createUser(dtoToUser(userDto))
        );
    }

    @PatchMapping({"/{id}"})
    public UserDto updateUserById(@PathVariable Integer id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userToDto(userService.updateUserById(dtoToUser(userDto))
        );
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return listUserToListDto(userService.findAllUsers());
    }

    @GetMapping({"/{id}"})
    public UserDto getUserById(@PathVariable Integer id) {
        return userToDto(userService.getUserById(id));
    }

    @DeleteMapping({"/{id}"})
    public void deleteUserById(@PathVariable Integer id) {
        userService.deleteUserById(id);
    }
}