package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public OutgoingBookingDto createBooking(Long bookerId, IncomingBookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("Пользователь c id = " + bookerId + " не найден !");
        }
        if (!itemService.itemIsExistsById(itemId)) {
            throw new ItemNotFoundException("Вещь с ID " + itemId + " не найдена");
        }
        if (!itemService.itemIsAvailableById(itemId)) {
            throw new IncorrectParameterException("Вещь с ID " + itemId + " недоступна для бронирования");
        }
        if (Objects.equals(bookerId, itemService.getItemById(bookerId, itemId).getOwner())) {
            throw new ItemNotFoundException("Владелец не может забронировать свою вещь");
        }
        Booking booking = Booking.builder()
                .id(null)
                .booker(userService.getUserById(bookerId))
                .item(itemRepository.getReferenceById(itemId))
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .approved(BookingStatus.WAITING)
                .build();
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public OutgoingBookingDto considerationOfBooking(Long bookingId, Long ownerId, boolean available) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new UserNotFoundException("изменить состояние бронирования может только владелец вещи");
        }
        if (available && (booking.getApproved() != BookingStatus.APPROVED)) {
            booking.setApproved(BookingStatus.APPROVED);
        } else if (!available && (booking.getApproved() != BookingStatus.REJECTED)) {
            booking.setApproved(BookingStatus.REJECTED);
        } else {
            throw new IncorrectParameterException("Статус не изменился");
        }
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public OutgoingBookingDto getBookingById(Long userId, Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new UserNotFoundException("Бронь с id " + bookingId + " не найдена");
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(item.getOwner().getId(), userId)) {
            throw new UserNotFoundException("Только пользователь создавший бронь может просматривать ее");
        }
        return BookingMapper.bookingToDto(bookingRepository.getReferenceById(bookingId));
    }

    @Override
    public Booking findLastBooking(Long itemId, LocalDateTime start) {
        return bookingRepository.findLastBooking(itemId, start);
    }

    @Override
    public Booking findNextBooking(Long itemId, LocalDateTime start) {
        return bookingRepository.findNextBooking(itemId, start);
    }

    @Override
    public List<OutgoingBookingDto> getAllBookingByOwnerId(Long ownerId, String state, Short from, Short size) {
        if (!userService.userIsExistsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        List<Booking> bookingList = null;
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("start").descending());
        List<String> enumNames = Stream.of(Status.values())
                .map(Status::name)
                .collect(Collectors.toList());
        if (!enumNames.contains(state)) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        LocalDateTime time = LocalDateTime.now();
        switch (Status.valueOf(state)) {
            case WAITING:
            case REJECTED:
                bookingList = bookingRepository.getAllByItemOwnerIdAndApproved(ownerId, BookingStatus.valueOf(state),
                        paging);
                break;
            case APPROVED:
            case FUTURE:
            case ALL:
                bookingList = bookingRepository.getAllByItemOwnerId(ownerId, paging);
                break;
            case CURRENT:
                bookingList = bookingRepository.getAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, time, time,
                        paging);
                break;
            case PAST:
                bookingList = bookingRepository.getAllByItemOwnerIdAndEndBefore(ownerId, time, paging);
        }
        return BookingMapper.listBookingToListDto(bookingList);
    }

    @Override
    public List<OutgoingBookingDto> getAllBookingByUserId(Long userId, String state, Short from, Short size) {
        List<Booking> result;
        if (!userService.userIsExistsById(userId)) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<Booking> bookingList = null;
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("start").descending());
        List<String> enumNames = Stream.of(Status.values())
                .map(Status::name)
                .collect(Collectors.toList());
        if (!enumNames.contains(state)) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        LocalDateTime time = LocalDateTime.now();
        switch (Status.valueOf(state)) {
            case WAITING:
            case REJECTED:
                bookingList = bookingRepository.getAllByBookerIdAndApproved(userId,
                        BookingStatus.valueOf(state), paging);
                break;
            case APPROVED:
            case FUTURE:
            case ALL:
                bookingList = bookingRepository.getAllByBookerId(userId, paging);
                break;
            case CURRENT:
                bookingList = bookingRepository.getAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time,
                        paging);
                break;
            case PAST:
                bookingList = bookingRepository.getAllByBookerIdAndEndBefore(userId, time, paging);
        }
        result = bookingList;
        return BookingMapper.listBookingToListDto(result);
    }
}