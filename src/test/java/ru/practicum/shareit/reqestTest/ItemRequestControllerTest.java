package ru.practicum.shareit.reqestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    @MockBean
    private RequestMapper requestMapper;
    private static ItemRequest request;
    private static IncomingItemRequestDto incomingItemRequestDto;
    private static OutgoingItemRequestDto outgoingItemRequestDto;
    private static ItemRequest requestWithBlankDescription;
    private static ItemRequest requestWithDescriptionSize555;
    private static List<ItemRequest> requestsList;
    private static List<OutgoingItemRequestDto> requestsListDto;

    @BeforeAll
    static void beforeAll() {
        ShortItemDto shortItemDto = ShortItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        List<ShortItemDto> items = new ArrayList<>();
        items.add(shortItemDto);

        User requester = User.builder().id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(requester)
                .build();

        incomingItemRequestDto = IncomingItemRequestDto.builder()
                .description("description")
                .build();
        outgoingItemRequestDto = OutgoingItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(items)
                .build();

        requestWithBlankDescription = ItemRequest.builder()
                .id(1L)
                .description(" ")
                .created(LocalDateTime.now())
                .requester(requester)
                .build();

        requestWithDescriptionSize555 = ItemRequest.builder()
                .id(1L)
                .description("D".repeat(555))
                .created(LocalDateTime.now())
                .requester(requester)
                .build();

        requestsList = new ArrayList<>();
        requestsList.add(request);
        requestsListDto = new ArrayList<>();
        requestsListDto.add(outgoingItemRequestDto);
    }

    @Test
    void createItemRequestWithoutHeaderTest() throws Exception {
        when(requestService.createItemRequest(any(ItemRequest.class)))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/requests")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(requestService, never()).createItemRequest(any(ItemRequest.class));
        verify(requestMapper, never()).itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, never()).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void createItemRequestWithBlankDescriptionTest() throws Exception {
        when(requestService.createItemRequest(any(ItemRequest.class)))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        String jsonRequest = objectMapper.writeValueAsString(requestWithBlankDescription);
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).createItemRequest(any(ItemRequest.class));
        verify(requestMapper, never()).itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, never()).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void createItemRequestWithDescriptionSize555Test() throws Exception {
        when(requestService.createItemRequest(any(ItemRequest.class)))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        String jsonRequest = objectMapper.writeValueAsString(requestWithDescriptionSize555);
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).createItemRequest(any(ItemRequest.class));
        verify(requestMapper, never()).itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, never()).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(requestService.createItemRequest(any(ItemRequest.class)))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        String jsonRequest = objectMapper.writeValueAsString(incomingItemRequestDto);
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
        verify(requestService, times(1)).createItemRequest(any(ItemRequest.class));
        verify(requestMapper, times(1))
                .itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, times(1)).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void findOwnItemRequestsWithRequestWithoutHeaderTest() throws Exception {
        when(requestService.getAllItemRequestByRequester(anyLong()))
                .thenReturn(requestsList);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(requestService, never()).getAllItemRequestByRequester(anyLong());
        verify(requestMapper, never()).itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, never()).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void findOwnItemRequestsTest() throws Exception {
        when(requestService.getAllItemRequestByRequester(anyLong()))
                .thenReturn(requestsList);
        when(requestMapper.listRequestToListDto(requestsList))
                .thenReturn(requestsListDto);
        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(requestService, times(1)).getAllItemRequestByRequester(anyLong());
        verify(requestMapper, times(1)).listRequestToListDto(anyList());
    }

    @Test
    void findAllItemRequestsWithoutHeaderTest() throws Exception {
        when(requestService.getAllItemRequest(anyLong(), anyShort(), anyShort()))
                .thenReturn(requestsList);
        when(requestMapper.listRequestToListDto(requestsList))
                .thenReturn(requestsListDto);
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(requestService, never()).getAllItemRequest(anyLong(), anyShort(), anyShort());
        verify(requestMapper, never()).listRequestToListDto(anyList());
    }

    @Test
    void findAllItemRequestsTest() throws Exception {
        when(requestService.getAllItemRequest(anyLong(), anyShort(), anyShort()))
                .thenReturn(requestsList);
        when(requestMapper.listRequestToListDto(requestsList))
                .thenReturn(requestsListDto);
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(requestService, times(1)).getAllItemRequest(anyLong(), anyShort(), anyShort());
        verify(requestMapper, times(1)).listRequestToListDto(anyList());
    }

    @Test
    void findItemRequestsByIdWithRequestWithoutHeaderTest() throws Exception {
        when(requestService.findItemRequestsById(anyLong(), anyLong()))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        when(requestMapper.incomingItemRequestDtoToItemRequest(anyLong(), any(IncomingItemRequestDto.class)))
                .thenReturn(request);
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(requestService, never()).findItemRequestsById(anyLong(), anyLong());
        verify(requestMapper, never()).itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
        verify(requestMapper, never()).incomingItemRequestDtoToItemRequest(anyLong(),
                any(IncomingItemRequestDto.class));
    }

    @Test
    void findItemRequestsByIdTest() throws Exception {
        when(requestService.findItemRequestsById(anyLong(), anyLong()))
                .thenReturn(request);
        when(requestMapper.itemRequestToOutgoingItemRequestDto(any(ItemRequest.class)))
                .thenReturn(outgoingItemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
        verify(requestService, times(1)).findItemRequestsById(anyLong(), anyLong());
        verify(requestMapper, times(1))
                .itemRequestToOutgoingItemRequestDto(any(ItemRequest.class));
    }
}