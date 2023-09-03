package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ItemMapper {
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final UserService userService;

    public Item dtoToItem(ItemDto itemDto) {
        Long id = itemDto.getId();
        ItemRequest request = null;
        if (null != itemDto.getRequestId()) {
            request = itemRequestService.findItemRequestsById(itemDto.getOwner(), itemDto.getRequestId());
        }
        return Item.builder()
                .id(id)
                .name(itemDto.getName() != null ? itemDto.getName() : itemService.getItemById(id).getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : itemService.getItemById(id)
                        .getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : itemService.getItemById(id)
                        .getAvailable())
                .owner(userService.getUserById(itemDto.getOwner()))
                .itemRequest(request)
                .build();
    }

    public Comment dtoToComment(IncomingCommentDto incomingCommentDto) {
        return Comment.builder()
                .item(itemService.getItemById(incomingCommentDto.getItemId()))
                .booker(userService.getUserById(incomingCommentDto.getAuthorId()))
                .text(incomingCommentDto.getText())
                .created(incomingCommentDto.getCreated())
                .build();
    }

    public OutgoingCommentDto commentToDto(Comment comment) {
        return OutgoingCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getBooker().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<OutgoingCommentDto> listCommentToListDto(List<Comment> comments) {
        List<OutgoingCommentDto> listCommentDto = new ArrayList<>();
        comments.forEach(value -> listCommentDto.add(commentToDto(value)));
        return listCommentDto;
    }

    public ItemDto itemToDto(Long userId, Item item) {
        ShortBookingDto shortLastBooking = null;
        ShortBookingDto shortNextBooking = null;
        Long itemRequestId = null;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Booking lastBooking = bookingService.findLastBooking(item.getId(), LocalDateTime.now());
            Booking nextBooking = bookingService.findNextBooking(item.getId(), LocalDateTime.now());
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
                .comments(listCommentToListDto(itemService.findAllCommentsByItemId(item.getId())))
                .requestId(itemRequestId)
                .build();
    }

    public List<ItemDto> listItemToListDto(List<Item> items) {
        List<ItemDto> listItemDto = new ArrayList<>();
        items.forEach(value -> listItemDto.add(itemToDto(value.getOwner().getId(), value)));
        return listItemDto;
    }
}