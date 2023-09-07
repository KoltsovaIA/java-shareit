package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private static UserDto correctIncomingUserDto;
    private static UserDto userDtoWithoutName;
    private static UserDto userDtoWithBlankName;
    private static UserDto userDtoWithoutEmail;
    private static UserDto userDtoWithWrongEmail;
    private static UserDto userDtoWithWrongEmailNoDomainSecondLevel;
    private static UserDto userDtoWithWrongEmailNoDomainFirstLevel;
    private static UserDto userDtoWithWrongEmailNoAt;
    private static UserDto correctOutgoingUserDto;
    private static User correctUser;
    private static List<User> usersList;
    private static List<UserDto> usersListDto;

    @BeforeAll
    static void beforeAll() {
        correctIncomingUserDto = UserDto.builder()
                .name("user")
                .email("user@email.ru")
                .build();

        userDtoWithoutName = UserDto.builder()
                .email("user@email.ru")
                .build();

        userDtoWithBlankName = UserDto.builder()
                .name("")
                .email("user@email.ru")
                .build();

        userDtoWithoutEmail = UserDto.builder()
                .name("user")
                .build();

        userDtoWithWrongEmail = UserDto.builder()
                .name("user")
                .email("@email.ru")
                .build();

        userDtoWithWrongEmailNoDomainSecondLevel = UserDto.builder()
                .name("user")
                .email("user@.ru")
                .build();

        userDtoWithWrongEmailNoDomainFirstLevel = UserDto.builder()
                .name("user")
                .email("user@mail.")
                .build();

        userDtoWithWrongEmailNoAt = UserDto.builder()
                .name("user")
                .email("user.mail.ru")
                .build();

        correctOutgoingUserDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        UserDto correctOutgoingUserDto2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        correctUser = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        User correctUser2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        usersList = new ArrayList<>();
        usersList.add(correctUser);
        usersList.add(correctUser2);

        usersListDto = new ArrayList<>();
        usersListDto.add(correctOutgoingUserDto);
        usersListDto.add(correctOutgoingUserDto2);
    }

    @Test
    void createUserWithoutNameTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithoutName);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithBlankNameTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithBlankName);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithoutEmailTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithoutEmail);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithWrongEmailTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithWrongEmail);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithWrongEmailNoDomainSecondLevelTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithWrongEmailNoDomainSecondLevel);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithWrongEmailNoDomainFirstLevelTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithWrongEmailNoDomainFirstLevel);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserWithWrongEmailNoAtTest() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDtoWithWrongEmailNoAt);
        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(User.class));
        verify(userMapper, never()).userToDto(any(User.class));
        verify(userMapper, never()).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUserTest() throws Exception {
        when(userMapper.dtoToUser(any(UserDto.class)))
                .thenReturn(correctUser);
        when(userService.createUser(any(User.class)))
                .thenReturn(correctUser);
        when(userMapper.userToDto(any(User.class)))
                .thenReturn(correctOutgoingUserDto);
        String jsonUser = objectMapper.writeValueAsString(correctIncomingUserDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctOutgoingUserDto.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingUserDto.getName()))
                .andExpect(jsonPath("$.email").value(correctOutgoingUserDto.getEmail()));

        verify(userService, times(1)).createUser(correctUser);
        verify(userMapper, times(1)).dtoToUser(any(UserDto.class));
        verify(userMapper, times(1)).userToDto(correctUser);
    }

    @Test
    void updateUserTest() throws Exception {
        when(userMapper.dtoToUser(any(UserDto.class)))
                .thenReturn(correctUser);
        when(userService.updateUser(any(User.class)))
                .thenReturn(correctUser);
        when(userMapper.userToDto(any(User.class)))
                .thenReturn(correctOutgoingUserDto);
        String jsonUser = objectMapper.writeValueAsString(correctUser);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctOutgoingUserDto.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingUserDto.getName()))
                .andExpect(jsonPath("$.email").value(correctOutgoingUserDto.getEmail()));
        verify(userService, times(1)).updateUser(any(User.class));
        verify(userMapper, times(1)).dtoToUser(any(UserDto.class));
        verify(userMapper, times(1)).userToDto(any(User.class));
    }

    @Test
    void updateUserWithoutNameTest() throws Exception {
        when(userService.updateUser(any(User.class)))
                .thenReturn(correctUser);
        when(userMapper.dtoToUser(any(UserDto.class)))
                .thenReturn(correctUser);
        when(userMapper.userToDto(any(User.class)))
                .thenReturn(correctOutgoingUserDto);
        String jsonUser = objectMapper.writeValueAsString(userDtoWithoutName);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctOutgoingUserDto.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingUserDto.getName()))
                .andExpect(jsonPath("$.email").value(correctOutgoingUserDto.getEmail()));
        verify(userService, times(1)).updateUser(any(User.class));
        verify(userMapper, never()).dtoToUser(correctOutgoingUserDto);
        verify(userMapper, times(1)).userToDto(correctUser);
    }

    @Test
    void updateUserWithoutEmailTest() throws Exception {
        when(userService.updateUser(any(User.class)))
                .thenReturn(correctUser);
        when(userMapper.dtoToUser(any(UserDto.class)))
                .thenReturn(correctUser);
        when(userMapper.userToDto(any(User.class)))
                .thenReturn(correctOutgoingUserDto);
        String jsonUser = objectMapper.writeValueAsString(userDtoWithoutEmail);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctOutgoingUserDto.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingUserDto.getName()))
                .andExpect(jsonPath("$.email").value(correctOutgoingUserDto.getEmail()));
        verify(userService, times(1)).updateUser(any(User.class));
        verify(userMapper, never()).dtoToUser(correctIncomingUserDto);
        verify(userMapper, times(1)).userToDto(correctUser);
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());
        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON));
        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(correctUser);
        when(userMapper.userToDto(any(User.class)))
                .thenReturn(correctOutgoingUserDto);
        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctOutgoingUserDto.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingUserDto.getName()))
                .andExpect(jsonPath("$.email").value(correctOutgoingUserDto.getEmail()));
        verify(userService, times(1)).getUserById(anyLong());
        verify(userMapper, times(1)).userToDto(correctUser);
    }

    @Test
    void findAllUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(usersList);
        when(userMapper.listUserToListDto(anyList()))
                .thenReturn(usersListDto);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[1].id").value(2L));
        verify(userService, times(1)).findAllUsers();
        verify(userMapper, times(1)).listUserToListDto(usersList);
    }
}