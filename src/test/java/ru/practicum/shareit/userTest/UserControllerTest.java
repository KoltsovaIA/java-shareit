package ru.practicum.shareit.userTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    private final User user = new User(1L, "user@email.ru", "User");

    @Test
    void createUserWhenUserDtoValidThenReturnedStatusIsOk() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).createUser(user);
    }

    @Test
    void createUserWhenUserDtoNotValidThenReturnedBadRequest() throws Exception {
        User user2 = new User(2L, "", "user2@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(user);
    }

    @Test
    void findByIdWhenUserIsExistThenReturnedStatusIsOk() throws Exception {
        Mockito.when(userService.getUserById(anyLong()))
                .thenReturn(user);

        String result = mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(user));
    }

    @Test
    void findByIdWhenUserIsNotExistThenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(userService.getUserById(100L))
                .thenThrow(new UserNotFoundException(String.format("User with ID = %d not found.", 1L)));
        mvc.perform(get("/users/{id}", 100L))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersTest() throws Exception {
        Mockito.when(userService.findAllUsers())
                .thenReturn(List.of(user));

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(user)));
    }

    @Test
    void updateUserTest() throws Exception {
        User updateUser = User.builder()
                .id(1L)
                .name("updateUser")
                .email("updateuser@email.com")
                .build();

        when(userService.createUser(any()))
                .thenReturn(updateUser);

        mvc.perform(patch("/users/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).createUser(updateUser);
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }
}