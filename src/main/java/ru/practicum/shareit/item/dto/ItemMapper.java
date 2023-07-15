package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemMapper {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    public Item dtoToItem(ItemDto itemDto) {
        Long id = itemDto.getId();
        return Item.builder()
                .id(id)
                .name(itemDto.getName() != null ? itemDto.getName() : itemService.getItemById(id).getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : itemService.getItemById(id).
                        getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : itemService.getItemById(id).
                        getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public Comment dtoToComment(IncomingCommentDto incomingCommentDto) {
        return Comment.builder()
                .itemId(incomingCommentDto.getItemId())
                .authorId(incomingCommentDto.getAuthorId())
                .text(incomingCommentDto.getText())
                .created(incomingCommentDto.getCreated())
                .build();
    }

    public OutgoingCommentDto commentToDto(Comment comment) {
        return OutgoingCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(userService.getUserById(comment.getAuthorId()).getName())
                .created(comment.getCreated())
                .build();
    }

    public List<OutgoingCommentDto> listCommentToListDto(List<Comment> comments) {
        List<OutgoingCommentDto> listCommentDto = new ArrayList<>();
        comments.forEach(value -> listCommentDto.add(commentToDto(value)));
        return listCommentDto;
    }

    public ItemDto itemToDto(Long userId, Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .lastBooking(item.getOwner() == userId ?
                        bookingService.findLastBooking(item.getId(), LocalDateTime.now()) :
                        null)
                .nextBooking(item.getOwner() == userId ?
                        bookingService.findNextBooking(item.getId(), LocalDateTime.now()) :
                        null)
                .comments(listCommentToListDto(itemService.findAllCommentsByItemId(item.getId())))
                .build();
    }

    public List<ItemDto> listItemToListDto(List<Item> items) {
        List<ItemDto> listItemDto = new ArrayList<>();
        items.forEach(value -> listItemDto.add(itemToDto(value.getOwner(), value)));
        return listItemDto;
    }
}