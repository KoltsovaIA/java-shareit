package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutgoingCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(Long userId, IncomingItemDto itemDto) {
        if (!userService.userIsExistsById(userId)) {
            throw new UserNotFoundException("Вещь не создана, так как такого пользователя не существует");
        }
        ItemRequest request = null;
        if (null != itemDto.getRequestId()) {
            request = itemRequestRepository.getReferenceById(itemDto.getRequestId());
        }
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(userService.getUserById(userId))
                .itemRequest(request)
                .build();
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now());
        return ItemMapper.itemToDto(userId, itemRepository.save(item), lastBooking, nextBooking,
                findAllCommentsByItemId(item.getId()));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, IncomingItemDto itemDto) {
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(),
                userId)) {
            throw new ItemNotFoundException("Пользователь не являющийся владельцем вещи, не может ее обновить");
        }
        ItemRequest request = null;
        if (null != itemDto.getRequestId()) {
            request = itemRequestRepository.getReferenceById(itemDto.getRequestId());
        }
        Item item = Item.builder()
                .id(itemId)
                .name(itemDto.getName() != null ? itemDto.getName() : itemRepository.getReferenceById(itemId).getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() :
                        itemRepository.getReferenceById(itemId).getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() :
                        itemRepository.getReferenceById(itemId).getAvailable())
                .owner(userService.getUserById(userId))
                .itemRequest(request)
                .build();
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now());
        return ItemMapper.itemToDto(userId, itemRepository.save(item), lastBooking, nextBooking,
                findAllCommentsByItemId(item.getId()));
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("вещь с id " + id + " не найдена");
        }
        Item item = itemRepository.getReferenceById(id);
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now());
        return ItemMapper.itemToDto(userId, item, lastBooking, nextBooking,
                findAllCommentsByItemId(item.getId()));
    }

    @Override
    public List<ItemDto> getAllByOwner(long owner) {
        List<Item> itemList = itemRepository.getAllByOwnerIdOrderByIdAsc(owner);
        List<ItemDto> listItemDto = new ArrayList<>();
        itemList.forEach(value -> listItemDto.add(ItemMapper.itemToDto(value.getOwner().getId(), value,
                bookingRepository.findLastBooking(value.getId(), LocalDateTime.now()),
                bookingRepository.findNextBooking(value.getId(), LocalDateTime.now()),
                findAllCommentsByItemId(value.getId()))));
        return listItemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items;
        List<ItemDto> listItemDto = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
            items.forEach(value -> listItemDto.add(ItemMapper.itemToDto(value.getOwner().getId(), value,
                    bookingRepository.findLastBooking(value.getId(), LocalDateTime.now()),
                    bookingRepository.findNextBooking(value.getId(), LocalDateTime.now()),
                    findAllCommentsByItemId(value.getId()))));
        }
        return listItemDto;
    }

    @Override
    public OutgoingCommentDto createComment(Long authorId, Long itemId, IncomingCommentDto incomingCommentDto) {
        if (bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(
                authorId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now(), null).isEmpty()) {
            throw new IncorrectParameterException("Нельзя создать комментарий к вещи которую не брали");
        }
        Comment comment = Comment.builder()
                .item(itemRepository.getReferenceById(itemId))
                .booker(userService.getUserById(authorId))
                .text(incomingCommentDto.getText())
                .created(LocalDateTime.now())
                .build();
        return ItemMapper.commentToDto(commentRepository.save(comment));
    }

    @Override
    public List<OutgoingCommentDto> findAllCommentsByItemId(Long itemId) {
        return ItemMapper.listCommentToListDto(commentRepository.getAllByItemId(itemId));
    }

    @Override
    public boolean itemIsExistsById(Long id) {
        return itemRepository.existsById(id);
    }

    @Override
    public boolean itemIsAvailableById(Long id) {
        return itemRepository.getReferenceById(id).getAvailable();
    }

    @Override
    public List<ItemDto> getAllByItemRequestId(Long requestId) {
        List<Item> itemList = itemRepository.getAllByItemRequestId(requestId);
        List<ItemDto> listItemDto = new ArrayList<>();
        itemList.forEach(value -> listItemDto.add(ItemMapper.itemToDto(value.getOwner().getId(), value,
                bookingRepository.findLastBooking(value.getId(), LocalDateTime.now()),
                bookingRepository.findNextBooking(value.getId(), LocalDateTime.now()),
                findAllCommentsByItemId(value.getId()))));
        return listItemDto;
    }
}