package ru.practicum.shareit.reqestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    private static ItemRequestRepository itemRequestRepository;
    private static ItemRequestService itemRequestService;
    private static UserService userService;
    private static ItemRequest itemRequest;
    private static ItemRequest otherItemRequest;
    private static User requester;
    private static LinkedList<ItemRequest> requests;
    private static LinkedList<ItemRequest> otherRequests;

    @BeforeEach
    void beforeAll() {
        userService = Mockito.mock(UserService.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService);
        requester = new User(10L, "user1@email.ru", "user1");
        User otherRequester = new User(11L, "user2@email.ru", "user2");
        itemRequest = new ItemRequest(1L, "new request", LocalDateTime.now(), requester);
        otherItemRequest = new ItemRequest(2L, "other request", LocalDateTime.now(), otherRequester);
        requests = new LinkedList<>(List.of(itemRequest));
        otherRequests = new LinkedList<>(List.of(otherItemRequest));
    }

    @Test
    void createItemRequestTest() {
        when(userService.userIsExistsById(itemRequest.getRequester().getId()))
                .thenReturn(true);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        assertEquals(itemRequest, itemRequestService.createItemRequest(itemRequest));
        assertThrows(UserNotFoundException.class, () -> itemRequestService.createItemRequest(otherItemRequest),
                "Метод createItemRequest работает некорректно при попытке сохранить запрос " +
                        "от несуществующего пользователя");
    }

    @Test
    void getAllItemRequestByRequesterTest() {
        when(userService.userIsExistsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.getAllByRequesterId(requester.getId()))
                .thenReturn(requests);
        assertEquals(requests, itemRequestService.getAllItemRequestByRequester(requester.getId()));
    }

    @Test
    void getAllItemRequestTest() {
        when(userService.userIsExistsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(eq(requester.getId()), any(Pageable.class)))
                .thenReturn(otherRequests);
        assertEquals(otherRequests, itemRequestService.getAllItemRequest(requester.getId(), (short) 0, (short) 30));
    }

    @Test
    void findItemRequestsByIdTest() {
        when(userService.userIsExistsById(requester.getId()))
                .thenReturn(true);
        when(itemRequestRepository.existsById(itemRequest.getId()))
                .thenReturn(true);
        when(itemRequestRepository.getReferenceById(itemRequest.getId()))
                .thenReturn(itemRequest);
        assertEquals(itemRequest, itemRequestService.findItemRequestsById(requester.getId(), itemRequest.getId()));
        assertThrows(ItemNotFoundException.class, () -> itemRequestService.findItemRequestsById(requester.getId(),
                        otherItemRequest.getId()),
                "Метод findItemRequestsById работает некорректно при попытке получить запрос " +
                        "которого не существует");
    }
}