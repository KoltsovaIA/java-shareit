/*
package ru.practicum.shareit.ItemTest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;


class ItemServiceTest {
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    UserService userService = Mockito.mock(UserService.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @InjectMocks
    ItemService itemService = new ItemServiceImpl(itemRepository, userService, commentRepository, bookingRepository);

    private Item item;
    private Long owner;
    private Long booker;
    private Long wrongOwner;
    private Long wrongBooker;
    private Long wrongItemId;

    @BeforeEach
    void setUp() {
        owner = 1L;
        booker = 2L;
        wrongOwner = 8888L;
        wrongBooker = 7777L;
        wrongItemId = 9999L;
        item = new Item(1L, "Item", "Description of item", true, owner, );
    }

    @Test
    void createItem() {
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito.when(userService.userIsExistsById(owner))
                .thenReturn(true);
        assertEquals(itemService.createItem(item), item);
        item.setOwner(wrongOwner);
        assertThrows(UserNotFoundException.class, () -> itemService.createItem(item),
                "Метод create работает некорректно при попытке получить пользователя с несуществующим id");
    }

    @Test
    void updateItem() {
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito.when(userService.userIsExistsById(owner))
                .thenReturn(true);
        Mockito.when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        assertEquals(itemService.updateItem(item), item);
        Item wrongItem = new Item(item.getId(), "Item", "Description of item", true, wrongOwner);
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(wrongItem),
                "Метод update работает некорректно при попытке обновить вещь не владельцем");
    }

    @Test
    void getItemById() {
        Mockito.when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        Mockito.when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        assertEquals(itemService.getItemById(item.getId()), item);
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(wrongItemId),
                "Метод getItemById работает некорректно при попытке получить вещь с несуществующим id");
    }

    @Test
    void searchItems() {
        String text = "";
        Mockito.when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(),
                        anyString()))
                .thenReturn(java.util.List.of(item));
        assertEquals(itemService.searchItems(text), new ArrayList<>());
        text = "of";
        assertEquals(itemService.searchItems(text), java.util.List.of(item));
    }

    @Test
    void createComment() {
        Comment comment = new Comment(1L, "comment1", 1L, 1L, LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item.getId(), 1L,
                BookingStatus.APPROVED);
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(anyLong(),
                        eq(1L), any(), any()))
                .thenReturn(new LinkedList<>(List.of(booking)));
        assertEquals(itemService.createComment(comment), comment);
        Comment comment2 = new Comment(1L, "comment1", 2L, 1L, LocalDateTime.now());
        assertThrows(IncorrectParameterException.class, () -> itemService.createComment(comment2),
                "Метод createComment работает некорректно при попытке создать комментарий "
                        + "к вещи которую небрали");
    }

    @Test
    void getAllByOwnerTest() {
        Mockito.when(itemRepository.getAllByOwnerId(anyLong()))
                .thenReturn(java.util.List.of(item));
        assertEquals(itemService.getAllByOwner(owner), java.util.List.of(item));
    }

    @Test
    void findAllCommentsByItemIdTest() {
        Comment comment = new Comment(1L, "comment1", 1L, 1L, LocalDateTime.now());
        Mockito.when(commentRepository.getAllByItemId(item.getId()))
                .thenReturn(new LinkedList<>(List.of(comment)));
        assertEquals(itemService.findAllCommentsByItemId(1L), new LinkedList<>(List.of(comment)));
        assertEquals(itemService.findAllCommentsByItemId(2L), new LinkedList<>());
    }

    @Test
    void itemIsExistsByIdTest() {
        Mockito.when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        assertTrue(itemService.itemIsExistsById(1L));
        assertFalse(itemService.itemIsExistsById(2L));
    }

    @Test
    void itemIsAvailableByIdTest() {
        Mockito.when(itemRepository.getReferenceById(item.getId()))
                        .thenReturn(item);
        assertEquals(itemService.itemIsAvailableById(1L), item.getAvailable());
    }
}*/
