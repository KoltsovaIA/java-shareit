package ru.practicum.shareit.reqestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requestDto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    private static ItemRepository itemRepository;
    private static ItemRequestRepository itemRequestRepository;
    private static ItemRequestService itemRequestService;
    private static UserRepository userRepository;
    private static IncomingItemRequestDto incomingItemRequestDto;
    private static ItemRequest itemRequest;
    private static OutgoingItemRequestDto outgoingItemRequestDto;
    private static ItemRequest otherItemRequest;
    private static User requester;
    private static User badRequester;
    private static LinkedList<ItemRequest> requests;
    private static LinkedList<OutgoingItemRequestDto> outgoingRequestsDto;
    private static LinkedList<ItemRequest> otherRequests;
    private static LinkedList<OutgoingItemRequestDto> outgoingOtherRequestsDto;

    @BeforeEach
    void beforeAll() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRepository, itemRequestRepository, userRepository);
        requester = new User(10L, "user1@email.ru", "user1");
        badRequester = new User(999L, "user999@email.ru", "user999");
        User otherRequester = new User(11L, "user2@email.ru", "user2");
        incomingItemRequestDto = IncomingItemRequestDto.builder()
                .description("description")
                .build();
        itemRequest = new ItemRequest(1L, incomingItemRequestDto.getDescription(), LocalDateTime.now(), requester);
        outgoingItemRequestDto = OutgoingItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
        otherItemRequest = new ItemRequest(2L, "other request", LocalDateTime.now(), otherRequester);
        OutgoingItemRequestDto outgoingOtherItemRequestDto = OutgoingItemRequestDto.builder()
                .id(otherItemRequest.getId())
                .description(otherItemRequest.getDescription())
                .created(otherItemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
        requests = new LinkedList<>(List.of(itemRequest));
        outgoingRequestsDto = new LinkedList<>(List.of(outgoingItemRequestDto));
        otherRequests = new LinkedList<>(List.of(otherItemRequest));
        outgoingOtherRequestsDto = new LinkedList<>(List.of(outgoingOtherItemRequestDto));
    }

    @Test
    void createItemRequestTest() {
        when(userRepository.existsById(itemRequest.getRequester().getId()))
                .thenReturn(true);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        when(itemRepository.getAllByItemRequestId(anyLong()))
                .thenReturn(new ArrayList<>());
        assertEquals(outgoingItemRequestDto, itemRequestService.createItemRequest(requester.getId(),
                incomingItemRequestDto));
        assertThrows(UserNotFoundException.class, () -> itemRequestService.createItemRequest(badRequester.getId(),
                        incomingItemRequestDto),
                "Метод createItemRequest работает некорректно при попытке сохранить запрос " +
                        "от несуществующего пользователя");
    }

    @Test
    void getAllItemRequestByRequesterTest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.getAllByRequesterId(requester.getId()))
                .thenReturn(requests);
        assertEquals(outgoingRequestsDto, itemRequestService.getAllItemRequestByRequester(requester.getId()));
    }

    @Test
    void getAllItemRequestTest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(eq(requester.getId()), any(Pageable.class)))
                .thenReturn(otherRequests);
        assertEquals(outgoingOtherRequestsDto, itemRequestService.getAllItemRequest(requester.getId(),
                (short) 0, (short) 30));
    }

    @Test
    void findItemRequestsByIdTest() {
        when(userRepository.existsById(requester.getId()))
                .thenReturn(true);
        when(itemRequestRepository.existsById(itemRequest.getId()))
                .thenReturn(true);
        when(itemRequestRepository.getReferenceById(itemRequest.getId()))
                .thenReturn(itemRequest);
        assertEquals(outgoingItemRequestDto, itemRequestService.findItemRequestsById(requester.getId(),
                itemRequest.getId()));
        assertThrows(ItemNotFoundException.class, () -> itemRequestService.findItemRequestsById(requester.getId(),
                        otherItemRequest.getId()),
                "Метод findItemRequestsById работает некорректно при попытке получить запрос " +
                        "которого не существует");
    }
}