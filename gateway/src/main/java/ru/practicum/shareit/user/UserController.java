package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.util.OnCreate;
import ru.practicum.shareit.util.OnUpdate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        return client.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserById(@PathVariable long userId,
                                                 @RequestBody @Validated(OnUpdate.class) UserDto userDto) {
        return client.updateUserById(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int from,
            @RequestParam(defaultValue = "32") @Min(1) @Max(32) int size) {
        return client.findAllUsers(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return client.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        return client.deleteUserById(userId);
    }
}