package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        checkUserIsExistById(itemRequest.getRequester().getId());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllItemRequestByRequester(Long requesterId) {
        checkUserIsExistById(requesterId);
        return itemRequestRepository.getAllByRequesterId(requesterId);
    }

    @Override
    public List<ItemRequest> getAllItemRequest(Long requesterId, Short from, Short size) {
        checkUserIsExistById(requesterId);
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("created").descending());
        return itemRequestRepository.findAllByRequesterIdNot(requesterId, paging);
    }

    @Override
    public ItemRequest findItemRequestsById(Long userId, Long requestId) {
        checkUserIsExistById(userId);
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ItemNotFoundException("такого запроса не существует");
        }
        return itemRequestRepository.getReferenceById(requestId);
    }

    private void checkUserIsExistById(Long userId) {
        if (!userService.userIsExistsById(userId)) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }
}