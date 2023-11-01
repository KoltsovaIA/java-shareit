package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requestDto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OutgoingItemRequestDto createItemRequest(Long requesterId, IncomingItemRequestDto itemRequestDto) {
        checkUserIsExistById(requesterId);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .requester(userRepository.getReferenceById(requesterId))
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .build();
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        List<Item> items = itemRepository.getAllByItemRequestId(savedItemRequest.getId());
        return RequestMapper.itemRequestToOutgoingItemRequestDto(savedItemRequest, items);
    }

    @Override
    public List<OutgoingItemRequestDto> getAllItemRequestByRequester(Long requesterId) {
        checkUserIsExistById(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequesterId(requesterId);
        List<OutgoingItemRequestDto> listItemRequestDto = new LinkedList<>();
        itemRequests.forEach(value -> listItemRequestDto.add(
                RequestMapper.itemRequestToOutgoingItemRequestDto(value,
                        itemRepository.getAllByItemRequestId(value.getId()))));
        return listItemRequestDto;
    }

    @Override
    public List<OutgoingItemRequestDto> getAllItemRequest(Long requesterId, Short from, Short size) {
        checkUserIsExistById(requesterId);
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(requesterId, paging);
        List<OutgoingItemRequestDto> listItemRequestDto = new LinkedList<>();
        itemRequests.forEach(value -> listItemRequestDto.add(
                RequestMapper.itemRequestToOutgoingItemRequestDto(value,
                        itemRepository.getAllByItemRequestId(value.getId()))));
        return listItemRequestDto;
    }

    @Override
    public OutgoingItemRequestDto findItemRequestsById(Long userId, Long requestId) {
        checkUserIsExistById(userId);
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ItemNotFoundException("такого запроса не существует");
        }
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        List<Item> items = itemRepository.getAllByItemRequestId(itemRequest.getId());
        return RequestMapper.itemRequestToOutgoingItemRequestDto(itemRequest, items);
    }

    private void checkUserIsExistById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }
}