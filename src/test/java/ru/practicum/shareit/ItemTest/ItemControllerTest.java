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
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    private static ItemDto correctIncomingItemDto;
    private static ItemDto correctOutgoingItemDto;
    private static Item item;
    private static ItemDto itemDtoWithNoName;
    private static ItemDto itemDtoWithNoDescription;
    private static ItemDto itemDtoWithNoAvailable;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static List<Item> itemsList;
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

        correctOutgoingItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto correctOutgoingItemDto2 = ItemDto.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
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
        itemsList = new ArrayList<>();
        itemsList.add(item);
        itemsList.add(item2);

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
        verify(itemService, never()).createItem(any(Item.class));
    }

    @Test
    void createItemWithoutNameTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoName);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(any(Item.class));
    }

    @Test
    void createItemWithoutDescriptionTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoDescription);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(any(Item.class));
    }

    @Test
    void createItemWithoutAvailableTest() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemDtoWithNoAvailable);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createItem(any(Item.class));
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(Item.class)))
                .thenReturn(item);
        when(itemMapper.itemToDto(eq(item.getOwner().getId()), any(Item.class)))
                .thenReturn(correctOutgoingItemDto);
        when(itemMapper.dtoToItem(any(ItemDto.class)))
                .thenReturn(item);
        String jsonItem = objectMapper.writeValueAsString(correctIncomingItemDto);
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
        verify(itemService, times(1)).createItem(any(Item.class));
        verify(itemMapper, times(1)).itemToDto(eq(item.getOwner().getId()), any(Item.class));
        verify(itemMapper, times(1)).dtoToItem(any(ItemDto.class));
    }

    @Test
    void updateWithoutHeaderTest() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).updateItem(any(Item.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(Item.class)))
                .thenReturn(item);
        when(itemMapper.itemToDto(eq(item.getOwner().getId()), any(Item.class)))
                .thenReturn(correctOutgoingItemDto);
        when(itemMapper.dtoToItem(any(ItemDto.class)))
                .thenReturn(item);
        String jsonItem = objectMapper.writeValueAsString(correctIncomingItemDto);
        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
        verify(itemService, times(1)).updateItem(any(Item.class));
        verify(itemMapper, times(1)).itemToDto(eq(item.getOwner().getId()), any(Item.class));
        verify(itemMapper, times(1)).dtoToItem(any(ItemDto.class));
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
                .thenReturn(itemsList);
        when(itemMapper.listItemToListDto(anyList()))
                .thenReturn(itemsListDto);
        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[1].id").value(2L));
        verify(itemService, times(1)).getAllByOwner(anyLong());
        verify(itemMapper, times(1)).listItemToListDto(anyList());
    }

    @Test
    void getItemByIdWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).getItemById(anyLong());
    }

    @Test
    void shouldGetByItemIdTest() throws Exception {
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        when(itemMapper.itemToDto(eq(item.getOwner().getId()), any(Item.class)))
                .thenReturn(correctOutgoingItemDto);
        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
        verify(itemService, times(1)).getItemById(anyLong());
        verify(itemMapper, times(1)).itemToDto(eq(item.getOwner().getId()), any(Item.class));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenReturn(itemsList);
        when(itemMapper.listItemToListDto(anyList()))
                .thenReturn(itemsListDto);
        mockMvc.perform(get("/items/search?text=text")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[1].id").value(2L));
        verify(itemService, times(1)).searchItems(anyString());
        verify(itemMapper, times(1)).listItemToListDto(anyList());
    }

    @Test
    void createCommentWithoutHeaderTest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(itemService, never()).createComment(any(Comment.class));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(any(Comment.class)))
                .thenReturn(comment);
        when(itemMapper.commentToDto(any(Comment.class)))
                .thenReturn(outgoingCommentDto);
        when(itemMapper.dtoToComment(any(IncomingCommentDto.class)))
                .thenReturn(comment);
        String jsonComment = objectMapper.writeValueAsString(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).createComment(any(Comment.class));
        verify(itemMapper, times(1)).commentToDto(any(Comment.class));
        verify(itemMapper, times(1)).dtoToComment(any(IncomingCommentDto.class));
    }
}