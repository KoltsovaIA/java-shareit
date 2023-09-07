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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ItemRequestMapperTest {
    private static ItemService itemService;
    private static UserService userService;
    private static RequestMapper requestMapper;

    private static User requester;
    private static IncomingItemRequestDto requestDto;
    private static OutgoingItemRequestDto outgoingItemRequestDto;
//    private static OutgoingItemRequestDto outgoingItemRequestDtoForList;
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
        outgoingItemRequestDto = new OutgoingItemRequestDto(itemRequest.getId(), "description",
                itemRequest.getCreated(), items);
    }

    @Test
    void incomingItemRequestDtoToItemRequestTest() {
        when(userService.getUserById(requester.getId()))
                .thenReturn(requester);
        itemRequest.setId(null);
        assertEquals(itemRequest.getId(), requestMapper
                .incomingItemRequestDtoToItemRequest(requester.getId(), requestDto).getId());
        assertEquals(itemRequest.getDescription(), requestMapper
                .incomingItemRequestDtoToItemRequest(requester.getId(), requestDto).getDescription());
        assertEquals(itemRequest.getCreated().getClass(), requestMapper
                .incomingItemRequestDtoToItemRequest(requester.getId(), requestDto).getCreated().getClass());
        assertEquals(itemRequest.getRequester(), requestMapper
                .incomingItemRequestDtoToItemRequest(requester.getId(), requestDto).getRequester());
    }

    @Test
    void itemToShortItemDtoTest() {
        item.getItemRequest().setId(1L);
        assertThat(requestMapper.itemToShortItemDto(item))
                .hasFieldOrPropertyWithValue("id", shortItemDto.getId())
                .hasFieldOrPropertyWithValue("name", shortItemDto.getName())
                .hasFieldOrPropertyWithValue("description", shortItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", shortItemDto.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", shortItemDto.getRequestId());
    }

    @Test
    void itemRequestToOutgoingItemRequestDtoTest() {
        List<Item> items = new LinkedList<>();
        items.add(item);
        when(itemService.getAllByItemRequestId(itemRequest.getId()))
                .thenReturn(items);
        assertThat(requestMapper.itemRequestToOutgoingItemRequestDto(itemRequest))
                .hasFieldOrPropertyWithValue("id", outgoingItemRequestDto.getId())
                .hasFieldOrPropertyWithValue("description", outgoingItemRequestDto.getDescription())
                .hasFieldOrPropertyWithValue("created", outgoingItemRequestDto.getCreated())
                .satisfies(OutgoingItemRequestDto -> assertThat(OutgoingItemRequestDto.getItems())
                        .isNotEmpty()
                        .hasSize(1)
                        .satisfies(list -> {
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("id",
                                    outgoingItemRequestDto.getItems().get(0).getId());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("name",
                                    outgoingItemRequestDto.getItems().get(0).getName());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("description",
                                    outgoingItemRequestDto.getItems().get(0).getDescription());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("description",
                                    outgoingItemRequestDto.getItems().get(0).getDescription());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("available",
                                    outgoingItemRequestDto.getItems().get(0).getAvailable());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("requestId",
                                    outgoingItemRequestDto.getItems().get(0).getRequestId());
                        })
                );

    }

    @Test
    void listRequestToListDtoTest() {
        List<Item> items = new LinkedList<>();
        items.add(item);
        when(itemService.getAllByItemRequestId(itemRequest.getId()))
                .thenReturn(items);
        List<ItemRequest> itemRequests = new LinkedList<>();
        itemRequests.add(itemRequest);
        assertThat(requestMapper.listRequestToListDto(itemRequests))
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", outgoingItemRequestDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description",
                            outgoingItemRequestDto.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("created",
                            outgoingItemRequestDto.getCreated());
                    assertThat(list.get(0)).satisfies(OutgoingItemRequestDto -> assertThat(OutgoingItemRequestDto.getItems())
                            .isNotEmpty()
                            .hasSize(1)
                            .satisfies(listItems -> {
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("id",
                                        outgoingItemRequestDto.getItems().get(0).getId());
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("name",
                                        outgoingItemRequestDto.getItems().get(0).getName());
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("description",
                                        outgoingItemRequestDto.getItems().get(0).getDescription());
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("description",
                                        outgoingItemRequestDto.getItems().get(0).getDescription());
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("available",
                                        outgoingItemRequestDto.getItems().get(0).getAvailable());
                                assertThat(listItems.get(0)).hasFieldOrPropertyWithValue("requestId",
                                        outgoingItemRequestDto.getItems().get(0).getRequestId());
                            })
                    );
                });
    }
}