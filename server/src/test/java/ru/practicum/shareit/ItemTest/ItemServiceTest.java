package ru.practicum.shareit.ItemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.itemDto.IncomingCommentDto;
import ru.practicum.shareit.itemDto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
    private static ItemRequestRepository itemRequestRepository;
    private static UserService userService;
    private static CommentRepository commentRepository;
    private static BookingRepository bookingRepository;
    private static ItemService itemService;
    private static Item item;
    private static IncomingItemDto itemDto;
    private static ItemDto outgoingItemDto;
    private static ItemRequest itemRequest;
    private static User owner;
    private static User booker;
    private static User wrongOwner;
    private static Long wrongItemId;

    @BeforeEach
    void beforeAll() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userService, commentRepository, bookingRepository,
                itemRequestRepository);
        owner = new User(1L, "user1@email.ru", "user1");
        booker = new User(2L, "user2@email.ru", "user2");
        wrongOwner = new User(8888L, "user8888@email.ru", "user8888");
        User wrongBooker = new User(7777L, "user7777@email.ru", "user7777");
        wrongItemId = 9999L;
        itemDto = IncomingItemDto.builder()
                .name("Item")
                .description("Description of item")
                .available(true)
                .build();
        item = new Item(1L, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                owner, null);
        outgoingItemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .comments(new ArrayList<>())
                .requestId(null)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(wrongBooker)
                .description("item request")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userService.userIsExistsById(owner.getId()))
                .thenReturn(true);
        when(bookingRepository.findLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findNextBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(itemService.findAllCommentsByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        when(itemRequestRepository.getReferenceById(anyLong()))
                .thenReturn(itemRequest);
        assertEquals(outgoingItemDto, itemService.createItem(owner.getId(), itemDto));
        assertThrows(UserNotFoundException.class, () -> itemService.createItem(wrongOwner.getId(), itemDto),
                "Метод create работает некорректно при попытке получить пользователя с несуществующим id");
        itemDto.setRequestId(itemRequest.getId());
        assertEquals(outgoingItemDto, itemService.createItem(owner.getId(), itemDto));
    }

    @Test
    void updateItem() {
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        when(userService.userIsExistsById(owner.getId()))
                .thenReturn(true);
        when(bookingRepository.findLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findNextBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(itemService.findAllCommentsByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        when(itemRequestRepository.getReferenceById(anyLong()))
                .thenReturn(itemRequest);
        assertEquals(outgoingItemDto, itemService.updateItem(owner.getId(), item.getId(), itemDto));
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(wrongOwner.getId(), item.getId(),
                itemDto), "Метод update работает некорректно при попытке обновить вещь не владельцем");
        itemDto.setRequestId(itemRequest.getId());
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        assertEquals(outgoingItemDto, itemService.updateItem(owner.getId(), item.getId(), itemDto));
    }

    @Test
    void getItemById() {
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        when(bookingRepository.findLastBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findNextBooking(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(itemService.findAllCommentsByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        assertEquals(outgoingItemDto, itemService.getItemById(owner.getId(), item.getId()));
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(owner.getId(), wrongItemId),
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
        assertEquals(java.util.List.of(outgoingItemDto), itemService.searchItems(text));
    }

    @Test
    void createComment() {
        IncomingCommentDto incomingCommentDto = IncomingCommentDto.builder()
                .text("comment1")
                .build();
        Comment comment = new Comment(1L, incomingCommentDto.getText(), item, booker, LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, booker,
                BookingStatus.APPROVED);
        OutgoingCommentDto outgoingCommentDto = OutgoingCommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getBooker().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(eq(booker.getId()),
                eq(item.getId()), any(), any(), eq(null)))
                .thenReturn(new LinkedList<>(List.of(booking)));
        assertEquals(outgoingCommentDto, itemService.createComment(booker.getId(), item.getId(), incomingCommentDto));
        assertThrows(IncorrectParameterException.class, () -> itemService.createComment(booker.getId(), wrongItemId,
                        incomingCommentDto),
                "Метод createComment работает некорректно при попытке создать комментарий "
                        + "к вещи которую небрали");
    }

    @Test
    void getAllByOwnerTest() {
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(java.util.List.of(item));
        assertEquals(java.util.List.of(outgoingItemDto), itemService.getAllByOwner(owner.getId()));
    }

    @Test
    void findAllCommentsByItemIdTest() {
        Comment comment = new Comment(1L, "comment1", item, booker, LocalDateTime.now());
        OutgoingCommentDto outgoingCommentDto = OutgoingCommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getBooker().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        when(commentRepository.getAllByItemId(item.getId()))
                .thenReturn(new LinkedList<>(List.of(comment)));
        assertEquals(new LinkedList<>(List.of(outgoingCommentDto)), itemService.findAllCommentsByItemId(item.getId()));
        assertEquals(new LinkedList<>(), itemService.findAllCommentsByItemId(wrongItemId));
    }

    @Test
    void itemIsExistsByIdTest() {
        when(itemRepository.existsById(item.getId()))
                .thenReturn(true);
        assertTrue(itemService.itemIsExistsById(item.getId()));
        assertFalse(itemService.itemIsExistsById(wrongItemId));
    }

    @Test
    void itemIsAvailableByIdTest() {
        when(itemRepository.getReferenceById(item.getId()))
                .thenReturn(item);
        assertEquals(item.getAvailable(), itemService.itemIsAvailableById(item.getId()));
        item.setAvailable(false);
        assertEquals(item.getAvailable(), itemService.itemIsAvailableById(item.getId()));
    }

    @Test
    void getAllByItemRequestIdTest() {
        when(itemRepository.getAllByItemRequestId(anyLong()))
                .thenReturn(java.util.List.of(item));
        assertEquals(java.util.List.of(outgoingItemDto), itemService.getAllByItemRequestId(1L));
    }
}