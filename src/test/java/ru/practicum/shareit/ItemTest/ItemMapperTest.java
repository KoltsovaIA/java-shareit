package ru.practicum.shareit.ItemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ItemMapperTest {
    private static ItemService itemService;
    private static ItemRequestService itemRequestService;
    private static BookingService bookingService;
    private static UserService userService;
    private static ItemMapper itemMapper;
    private static Item item;
    private static User user;
    private static ItemRequest itemRequest;
    private static ItemDto itemDto;
    private static ItemDto itemDtoWithNullDescription;
    private static ItemDto itemDtoWithNullAvailable;
    private static Booking lastBooking;
    private static Booking nextBooking;
    private static List<Comment> commentList;
    private static IncomingCommentDto incomingCommentDto;
    private static Comment comment;
    private static OutgoingCommentDto outgoingCommentDto;

    @BeforeEach
    void beforeEach() {
        itemService = Mockito.mock(ItemService.class);
        userService = Mockito.mock(UserService.class);
        itemRequestService = Mockito.mock(ItemRequestService.class);
        bookingService = Mockito.mock(BookingService.class);
        itemMapper = new ItemMapper(itemService, itemRequestService, bookingService, userService);
        user = new User(5L, "user@email.ru", "User1");
        User requester = new User(100L, "user100@email.ru", "User100");
        User booker = new User(8L, "user8@email.ru", "User8");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), requester);
        item = new Item(1L, "name", "description", true, user, itemRequest);
        lastBooking = new Booking(1L, LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1),
                item, booker, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                item, booker, BookingStatus.APPROVED);
        ShortBookingDto shortLastBooking = new ShortBookingDto(lastBooking.getId(), booker.getId());
        ShortBookingDto shortNextBooking = new ShortBookingDto(nextBooking.getId(), booker.getId());
        incomingCommentDto = new IncomingCommentDto(1L, "text", 1L, 1L, LocalDateTime.now());
        comment = new Comment(null, "text", item, user, incomingCommentDto.getCreated());
        commentList = List.of(comment);
        outgoingCommentDto = new OutgoingCommentDto(null, "text", user.getName(), comment.getCreated());
        List<OutgoingCommentDto> outgoingCommentDtoList = List.of(outgoingCommentDto);
        itemDto = new ItemDto(1L, "name", "description", true, 5L, shortLastBooking,
                shortNextBooking, outgoingCommentDtoList, itemRequest.getId());
        itemDtoWithNullDescription = new ItemDto(1L, "name", null, true, 5L,
                null, null, null, itemRequest.getId());
        itemDtoWithNullAvailable = new ItemDto(1L, "name", "description", null, 5L,
                null, null, null, itemRequest.getId());
    }

    @Test
    void dtoToItemTest() {
        when(userService.getUserById(itemDto.getOwner()))
                .thenReturn(user);
        when(itemRequestService.findItemRequestsById(itemDto.getOwner(), itemDto.getRequestId()))
                .thenReturn(itemRequest);
        assertThat(itemMapper.dtoToItem(itemDto))
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("owner", item.getOwner())
                .hasFieldOrPropertyWithValue("itemRequest", item.getItemRequest());
        when(itemService.getItemById(itemDtoWithNullDescription.getId()))
                .thenReturn(item);
        assertThat(itemMapper.dtoToItem(itemDtoWithNullDescription))
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("owner", item.getOwner())
                .hasFieldOrPropertyWithValue("itemRequest", item.getItemRequest());
        assertThat(itemMapper.dtoToItem(itemDtoWithNullAvailable))
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("owner", item.getOwner())
                .hasFieldOrPropertyWithValue("itemRequest", item.getItemRequest());
    }

    @Test
    void dtoToCommentTest() {
        when(itemService.getItemById(incomingCommentDto.getItemId()))
                .thenReturn(item);
        when(userService.getUserById(incomingCommentDto.getAuthorId()))
                .thenReturn(user);
        assertThat(itemMapper.dtoToComment(incomingCommentDto))
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("item", comment.getItem())
                .hasFieldOrPropertyWithValue("booker", comment.getBooker())
                .hasFieldOrPropertyWithValue("created", comment.getCreated());
    }

    @Test
    void commentToDtoTest() {
        assertThat(itemMapper.commentToDto(comment))
                .hasFieldOrPropertyWithValue("id", outgoingCommentDto.getId())
                .hasFieldOrPropertyWithValue("text", outgoingCommentDto.getText())
                .hasFieldOrPropertyWithValue("authorName", outgoingCommentDto.getAuthorName())
                .hasFieldOrPropertyWithValue("created", outgoingCommentDto.getCreated());
    }

    @Test
    void listCommentToListDtoTest() {
        List<Comment> listComment = new ArrayList<>();
        listComment.add(comment);
        assertThat(itemMapper.listCommentToListDto(listComment))
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", outgoingCommentDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("text", outgoingCommentDto.getText());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("authorName",
                            outgoingCommentDto.getAuthorName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("created", outgoingCommentDto.getCreated());
                });
    }

    @Test
    void itemToDtoTest() {
        when(bookingService.findLastBooking(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingService.findNextBooking(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        when(itemService.findAllCommentsByItemId(item.getId()))
                .thenReturn(commentList);
        assertThat(itemMapper.itemToDto(item.getOwner().getId(), item))
                .hasFieldOrPropertyWithValue("id", itemDto.getId())
                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable())
                .hasFieldOrPropertyWithValue("owner", itemDto.getOwner())
                .hasFieldOrPropertyWithValue("requestId", itemDto.getRequestId())
                .satisfies(ItemDto -> {
                    assertThat(ItemDto.getLastBooking()).hasFieldOrPropertyWithValue("id",
                            itemDto.getLastBooking().getId());
                    assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("bookerId",
                            itemDto.getLastBooking().getBookerId());
                })
                .satisfies(ItemDto -> {
                    assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("id",
                            itemDto.getNextBooking().getId());
                    assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("bookerId",
                            itemDto.getNextBooking().getBookerId());
                })
                .satisfies(ItemDto -> assertThat(ItemDto.getComments())
                        .isNotEmpty()
                        .hasSize(1)
                        .satisfies(list -> {
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("id",
                                    itemDto.getComments().get(0).getId());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("text",
                                    itemDto.getComments().get(0).getText());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("authorName",
                                    itemDto.getComments().get(0).getAuthorName());
                            assertThat(list.get(0)).hasFieldOrPropertyWithValue("created",
                                    itemDto.getComments().get(0).getCreated());
                        }));
    }

    @Test
    void listItemToListDtoTest() {
        List<Item> listItem = new ArrayList<>();
        listItem.add(item);
        when(bookingService.findLastBooking(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingService.findNextBooking(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        when(itemService.findAllCommentsByItemId(item.getId()))
                .thenReturn(commentList);
        assertThat(itemMapper.listItemToListDto(listItem))
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", itemDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", itemDto.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", itemDto.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("available", itemDto.getAvailable());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("owner", itemDto.getOwner());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("requestId", itemDto.getRequestId());
                    assertThat(list.get(0)).satisfies(ItemDto -> {
                        assertThat(ItemDto.getLastBooking()).hasFieldOrPropertyWithValue("id",
                                itemDto.getLastBooking().getId());
                        assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("bookerId",
                                itemDto.getLastBooking().getBookerId());
                    });
                    assertThat(list.get(0)).satisfies(ItemDto -> {
                        assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("id",
                                itemDto.getNextBooking().getId());
                        assertThat(ItemDto.getNextBooking()).hasFieldOrPropertyWithValue("bookerId",
                                itemDto.getNextBooking().getBookerId());
                    });
                    assertThat(list.get(0)).satisfies(ItemDto -> assertThat(ItemDto.getComments())
                            .isNotEmpty()
                            .hasSize(1)
                            .satisfies(listComments -> {
                                assertThat(listComments.get(0)).hasFieldOrPropertyWithValue("id",
                                        itemDto.getComments().get(0).getId());
                                assertThat(listComments.get(0)).hasFieldOrPropertyWithValue("text",
                                        itemDto.getComments().get(0).getText());
                                assertThat(listComments.get(0)).hasFieldOrPropertyWithValue("authorName",
                                        itemDto.getComments().get(0).getAuthorName());
                                assertThat(listComments.get(0)).hasFieldOrPropertyWithValue("created",
                                        itemDto.getComments().get(0).getCreated());
                            }));
                });
    }
}
