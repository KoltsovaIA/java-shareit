package ru.practicum.shareit.reqestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ItemRequestMapperTest {
    private static ItemService itemService;
    private static UserService userService;
    private static RequestMapper requestMapper;

    private static User requester;
    private static IncomingItemRequestDto requestDto;
    private static OutgoingItemRequestDto outgoingItemRequestDto;
    private static OutgoingItemRequestDto outgoingItemRequestDtoForList;
    private static ItemRequest itemRequest;
    private static Item item;
    private static ShortItemDto shortItemDto;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        requestMapper = new RequestMapper(itemService, userService);
        requester = new User(100L, "user100@email.ru", "User100");
        User owner = new User(2L, "user2@email.ru", "User2");
        requestDto = new IncomingItemRequestDto("description");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), requester);
        item = new Item(1L, "name", "description", true, owner, itemRequest);
        shortItemDto = new ShortItemDto(1L, "name", "description", true, 1L);
        List<ShortItemDto> items = new ArrayList<>();
        items.add(shortItemDto);
        itemRequest = new ItemRequest(null, "description", LocalDateTime.now(), null);
        outgoingItemRequestDto = new OutgoingItemRequestDto(null, "description", LocalDateTime.now(), items);
        outgoingItemRequestDtoForList = new OutgoingItemRequestDto(null, "description", LocalDateTime.now(), new ArrayList<>());

    }

    @Test
    void incomingItemRequestDtoToItemRequestTest() {
        when(userService.getUserById(requester.getId()))
                .thenReturn(requester);
        assertEquals(itemRequest.getId(), requestMapper
                .incomingItemRequestDtoToItemRequest(itemRequest.getId(), requestDto).getId());
        assertEquals(itemRequest.getDescription(), requestMapper
                .incomingItemRequestDtoToItemRequest(itemRequest.getId(), requestDto).getDescription());
        assertEquals(itemRequest.getCreated().getClass(), requestMapper
                .incomingItemRequestDtoToItemRequest(itemRequest.getId(), requestDto).getCreated().getClass());
        assertEquals(itemRequest.getRequester(), requestMapper
                .incomingItemRequestDtoToItemRequest(itemRequest.getId(), requestDto).getRequester());
    }

    @Test
    void itemToShortItemDtoTest() {
        assertEquals(shortItemDto, requestMapper.itemToShortItemDto(item));
    }

    @Test
    void itemRequestToOutgoingItemRequestDtoTest() {
        List<Item> items = new LinkedList<>();
        items.add(item);
        when(itemService.getAllByItemRequestId(itemRequest.getId()))
                .thenReturn(items);
        assertEquals(outgoingItemRequestDto, requestMapper.itemRequestToOutgoingItemRequestDto(itemRequest));
    }

    @Test
    void listRequestToListDtoTest() {
        LinkedList<OutgoingItemRequestDto> listItemRequestDto = new LinkedList<>();
        listItemRequestDto.add(outgoingItemRequestDtoForList);
        List<ItemRequest> itemRequests = new LinkedList<>();
        itemRequests.add(itemRequest);
        assertEquals(listItemRequestDto, requestMapper.listRequestToListDto(itemRequests));
    }
}