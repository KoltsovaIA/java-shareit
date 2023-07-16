package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item createItem(Item item) {
        if (!userService.userIsExistsById(item.getOwner())) {
            throw new UserNotFoundException("");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        if (itemRepository.getReferenceById(item.getId()).getOwner() != item.getOwner()) {
            throw new ItemNotFoundException("");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("вещь с id " + id + " не найдена");
        }
        return itemRepository.getReferenceById(id);
    }

    @Override
    public List<Item> getAllByOwner(long owner) {
        return itemRepository.getAllByOwner(owner);
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> items = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        }
        return items;
    }

    @Override
    public Comment createComment(Comment comment) {
        if (bookingRepository.getAllByBookerIdAndItemIdAndApprovedAndEndBeforeOrderByStartDesc(comment.getAuthorId(),
                comment.getItemId(), BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new IncorrectParameterException("Нельзя создать комментарий к вещи которую не брали");
        }
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllCommentsByItemId(Long itemId) {
        return commentRepository.getAllByItemId(itemId);
    }

    @Override
    public boolean itemIsExistsById(Long id) {
        return itemRepository.existsById(id);
    }

    @Override
    public boolean itemIsAvailableById(Long id) {
        return itemRepository.getReferenceById(id).getAvailable();
    }
}