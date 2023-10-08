package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemMapper {
    private ItemMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static OutgoingCommentDto commentToDto(Comment comment) {
        return OutgoingCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getBooker().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<OutgoingCommentDto> listCommentToListDto(List<Comment> comments) {
        List<OutgoingCommentDto> listCommentDto = new ArrayList<>();
        comments.forEach(value -> listCommentDto.add(commentToDto(value)));
        return listCommentDto;
    }

    public static ItemDto itemToDto(Long userId, Item item, Booking lastBooking, Booking nextBooking,
                             List<OutgoingCommentDto> commentList) {
        ShortBookingDto shortLastBooking = null;
        ShortBookingDto shortNextBooking = null;
        Long itemRequestId = null;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            if (lastBooking != null) {
                shortLastBooking = ShortBookingDto.builder()
                        .id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId())
                        .build();
            }
            if (nextBooking != null) {
                shortNextBooking = ShortBookingDto.builder()
                        .id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId())
                        .build();
            }
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .lastBooking(shortLastBooking)
                .nextBooking(shortNextBooking)
                .comments(commentList)
                .requestId(itemRequestId)
                .build();
    }
}