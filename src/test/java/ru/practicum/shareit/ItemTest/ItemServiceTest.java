package ru.practicum.shareit.ItemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemServiceTest {
    private static ItemRepository itemRepository;
    private static UserService userService;
    private static CommentRepository commentRepository;
    private static BookingRepository bookingRepository;
    private static ItemService itemService;
    private static Item item;
    private static User owner;
    private static User booker;
    private static User wrongOwner;
    private static User wrongBooker;
    private static Long wrongItemId;

    @BeforeEach
    void beforeAll() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userService, commentRepository, bookingRepository);
        owner = new User(1L, "user1@email.ru", "user1");
        booker = new User(2L, "user2@email.ru", "user2");
        wrongOwner = new User(8888L, "user8888@email.ru", "user8888");
        wrongBooker = new User(7777L, "user7777@email.ru", "user7777");
        wrongItemId = 9999L;
        item = new Item(1L, "Item", "Description of item", true, owner, null);
    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userService.userIsExistsById(owner.getId()))
                .thenReturn(true);
        assertEquals(item, itemService.createItem(item));
        item.setOwner(wrongOwner);
        assertThrows(UserNotFoundException.class, () -> itemService.createItem(item),
                "Метод create работает некорректно при попытке получить пользователя с несуществующим id");
    }

    @Test
    void updateItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userService.userIsExistsById(owner.getId()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        assertEquals(item, itemService.updateItem(item));
        Item wrongItem = new Item(item.getId(), "Item", "Description of item",
                true, wrongOwner, null);
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(wrongItem),
                "Метод update работает некорректно при попытке обновить вещь не владельцем");
    }

    @Test
    void getItemById() {
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        assertEquals(item, itemService.getItemById(item.getId()));
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(wrongItemId),
                "Метод getItemById работает некорректно при попытке получить вещь с несуществующим id");
    }

    @Test
    void searchItems() {
        String text = "";
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(),
                anyString()))
                .thenReturn(java.util.List.of(item));
        assertEquals(new ArrayList<>(), itemService.searchItems(text));
        text = "of";
        assertEquals(java.util.List.of(item), itemService.searchItems(text));
    }

    @Test
    void createComment() {
        Comment comment = new Comment(1L, "comment1", item, booker, LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, booker,
                BookingStatus.APPROVED);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(eq(booker.getId()),
                eq(item.getId()), any(), any(), eq(null)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        assertEquals(comment, itemService.createComment(comment));
        Comment comment2 = new Comment(2L, "comment2", item, wrongBooker, LocalDateTime.now());
        assertThrows(IncorrectParameterException.class, () -> itemService.createComment(comment2),
                "Метод createComment работает некорректно при попытке создать комментарий "
                        + "к вещи которую небрали");
    }

    @Test
    void getAllByOwnerTest() {
        when(itemRepository.getAllByOwnerId(anyLong()))
                .thenReturn(java.util.List.of(item));
        assertEquals(java.util.List.of(item), itemService.getAllByOwner(owner.getId()));
    }

    @Test
    void findAllCommentsByItemIdTest() {
        Comment comment = new Comment(1L, "comment1", item, booker, LocalDateTime.now());
        when(commentRepository.getAllByItemId(item.getId()))
                .thenReturn(new LinkedList<>(List.of(comment)));
        assertEquals(new LinkedList<>(List.of(comment)), itemService.findAllCommentsByItemId(1L));
        assertEquals(new LinkedList<>(), itemService.findAllCommentsByItemId(2L));
    }

    @Test
    void itemIsExistsByIdTest() {
        when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        assertTrue(itemService.itemIsExistsById(1L));
        assertFalse(itemService.itemIsExistsById(2L));
    }

    @Test
    void itemIsAvailableByIdTest() {
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        assertEquals(item.getAvailable(), itemService.itemIsAvailableById(1L));
    }

    @Test
    void getAllByItemRequestIdTest() {
        when(itemRepository.getAllByItemRequestId(anyLong()))
                .thenReturn(java.util.List.of(item));
        assertEquals(java.util.List.of(item), itemService.getAllByItemRequestId(1L));
    }
}