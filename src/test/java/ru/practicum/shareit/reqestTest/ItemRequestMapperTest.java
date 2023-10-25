package ru.practicum.shareit.reqestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {
    private static OutgoingItemRequestDto outgoingItemRequestDto;
    private static ItemRequest itemRequest;
    private static Item item;
    private static ShortItemDto shortItemDto;

    @BeforeEach
    void setUp() {
        User requester = new User(100L, "user100@email.ru", "User100");
        User owner = new User(2L, "user2@email.ru", "User2");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), requester);
        item = new Item(1L, "name", "description", true, owner, itemRequest);
        shortItemDto = new ShortItemDto(1L, "name", "description", true, 1L);
        List<ShortItemDto> items = new ArrayList<>();
        items.add(shortItemDto);
        outgoingItemRequestDto = new OutgoingItemRequestDto(itemRequest.getId(), "description",
                itemRequest.getCreated(), items);
    }

    @Test
    void itemToShortItemDtoTest() {
        item.getItemRequest().setId(1L);
        assertThat(RequestMapper.itemToShortItemDto(item))
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
        assertThat(RequestMapper.itemRequestToOutgoingItemRequestDto(itemRequest, items))
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

}