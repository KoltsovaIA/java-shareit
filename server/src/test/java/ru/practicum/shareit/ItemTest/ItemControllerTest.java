package ru.practicum.shareit.ItemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.itemDto.IncomingCommentDto;
import ru.practicum.shareit.itemDto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static ItemDto correctIncomingItemDto;
    private static ItemDto correctOutgoingItemDto;
    private static Item item;
    private static ItemDto itemDtoWithNoName;
    private static ItemDto itemDtoWithNoDescription;
    private static ItemDto itemDtoWithNoAvailable;
    private static List<ItemDto> itemsListDto;
    private static Comment comment;
    private static OutgoingCommentDto outgoingCommentDto;

    @BeforeAll
    static void beforeAll() {
        correctIncomingItemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        item = Item.builder()
                .id(1L)
                .name(correctIncomingItemDto.getName())
                .description(correctIncomingItemDto.getDescription())
                .available(correctIncomingItemDto.getAvailable())
                .owner(new User(1L, "user1@mail.ru", "name"))
                .itemRequest(null)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(new User(1L, "user1@mail.ru", "name"))
                .itemRequest(null)
                .build();

        correctOutgoingItemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        ItemDto correctOutgoingItemDto2 = ItemDto.builder()
                .id(item2.getId())
                .name(item2.getName())
                .description(item2.getDescription())
                .available(item2.getAvailable())
                .build();

        itemDtoWithNoName = ItemDto.builder()
                .description("description")
                .available(true)
                .build();

        itemDtoWithNoDescription = ItemDto.builder()
                .name("name")
                .available(true)
                .build();

        itemDtoWithNoAvailable = ItemDto.builder()
                .name("name")
                .description("description")
                .build();

        itemsListDto = new ArrayList<>();
        itemsListDto.add(correctOutgoingItemDto);
        itemsListDto.add(correctOutgoingItemDto2);

        comment = Comment.builder()
                .text("comment")
                .build();

        outgoingCommentDto = OutgoingCommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    void createItemWithoutHeaderTest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).createItem(anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void createItemWithoutNameTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoName);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void createItemWithoutDescriptionTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoDescription);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void createItemWithoutAvailableTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoAvailable);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any(IncomingItemDto.class)))
                .thenReturn(correctOutgoingItemDto);
        String jsonItem = objectMapper.writeValueAsString(correctIncomingItemDto);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(correctIncomingItemDto.getName()))
                .andExpect(jsonPath("$.description").value(correctIncomingItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(correctIncomingItemDto.getAvailable()));
        verify(itemService, times(1)).createItem(anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void updateWithoutHeaderTest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).updateItem(anyLong(), anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(IncomingItemDto.class)))
                .thenReturn(correctOutgoingItemDto);
        String jsonItem = objectMapper.writeValueAsString(correctIncomingItemDto);
        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(correctIncomingItemDto.getName()))
                .andExpect(jsonPath("$.description").value(correctIncomingItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(correctIncomingItemDto.getAvailable()));
        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any(IncomingItemDto.class));
    }

    @Test
    void findAllByOwnerWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).getAllByOwner(anyLong());
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        when(itemService.getAllByOwner(anyLong()))
                .thenReturn(itemsListDto);
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(itemsListDto.size()))
                .andExpect(jsonPath("$.[0]").value(itemsListDto.get(0)))
                .andExpect(jsonPath("$.[1]").value(itemsListDto.get(1)));
        verify(itemService, times(1)).getAllByOwner(anyLong());
    }

    @Test
    void getItemByIdWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    void shouldGetByItemIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(correctOutgoingItemDto);
        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(correctOutgoingItemDto.getName()))
                .andExpect(jsonPath("$.description").value(correctOutgoingItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(correctOutgoingItemDto.getAvailable()));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenReturn(itemsListDto);
        mockMvc.perform(get("/items/search?text=text")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(itemsListDto.size()))
                .andExpect(jsonPath("$.[0]").value(itemsListDto.get(0)))
                .andExpect(jsonPath("$.[1]").value(itemsListDto.get(1)));
        verify(itemService, times(1)).searchItems(anyString());
    }

    @Test
    void createCommentWithoutHeaderTest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).createComment(anyLong(), anyLong(), any(IncomingCommentDto.class));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(IncomingCommentDto.class)))
                .thenReturn(outgoingCommentDto);
        String jsonComment = objectMapper.writeValueAsString(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(outgoingCommentDto.getText()));
        verify(itemService, times(1)).createComment(anyLong(), anyLong(),
                any(IncomingCommentDto.class));
    }
}