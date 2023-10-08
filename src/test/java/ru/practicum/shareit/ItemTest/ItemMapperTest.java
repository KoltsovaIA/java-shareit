package ru.practicum.shareit.ItemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {
    private static Item item;
    private static ItemDto itemDto;
    private static Booking lastBooking;
    private static Booking nextBooking;
    private static Comment comment;
    private static OutgoingCommentDto outgoingCommentDto;
    private static List<OutgoingCommentDto> outgoingCommentDtoList;

    @BeforeEach
    void beforeEach() {
        User user = new User(5L, "user@email.ru", "User1");
        User requester = new User(100L, "user100@email.ru", "User100");
        User booker = new User(8L, "user8@email.ru", "User8");
        ItemRequest itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), requester);
        item = new Item(1L, "name", "description", true, user, itemRequest);
        lastBooking = new Booking(1L, LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1),
                item, booker, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                item, booker, BookingStatus.APPROVED);
        ShortBookingDto shortLastBooking = new ShortBookingDto(lastBooking.getId(), booker.getId());
        ShortBookingDto shortNextBooking = new ShortBookingDto(nextBooking.getId(), booker.getId());
        comment = new Comment(null, "text", item, user, LocalDateTime.now());
        outgoingCommentDto = new OutgoingCommentDto(null, "text", user.getName(), comment.getCreated());
        outgoingCommentDtoList = List.of(outgoingCommentDto);
        itemDto = new ItemDto(1L, "name", "description", true, 5L, shortLastBooking,
                shortNextBooking, outgoingCommentDtoList, itemRequest.getId());
    }

    @Test
    void commentToDtoTest() {
        assertThat(ItemMapper.commentToDto(comment))
                .hasFieldOrPropertyWithValue("id", outgoingCommentDto.getId())
                .hasFieldOrPropertyWithValue("text", outgoingCommentDto.getText())
                .hasFieldOrPropertyWithValue("authorName", outgoingCommentDto.getAuthorName())
                .hasFieldOrPropertyWithValue("created", outgoingCommentDto.getCreated());
    }

    @Test
    void listCommentToListDtoTest() {
        List<Comment> listComment = new ArrayList<>();
        listComment.add(comment);
        assertThat(ItemMapper.listCommentToListDto(listComment))
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
        assertThat(ItemMapper.itemToDto(item.getOwner().getId(), item, lastBooking, nextBooking,
                outgoingCommentDtoList))
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
}
