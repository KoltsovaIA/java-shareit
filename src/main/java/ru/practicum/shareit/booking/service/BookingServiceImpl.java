package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
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

    @Override
    public Booking createBooking(Booking booking) {
        Long bookerId = booking.getBooker().getId();
        Long itemId = booking.getItem().getId();
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("пользователь c id = " + bookerId + " не найден !");
        }
        if (!itemService.itemIsExistsById(itemId)) {
            throw new ItemNotFoundException("Вещь с ID " + itemId + " не найдена");
        }
        if (!itemService.itemIsAvailableById(itemId)) {
            throw new IncorrectParameterException("Вещь с ID " + itemId + " недоступна для бронирования");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectParameterException("Время начала бронирования не может быть в прошлом");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new IncorrectParameterException("время окончания бронирования не может быть в прошлом");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new IncorrectParameterException("время окончания и начала бронирования не могут совпадать");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new IncorrectParameterException("время окончания бронирования не может быть раньше");
        }
        if (Objects.equals(bookerId, itemService.getItemById(itemId).getOwner().getId())) {
            throw new ItemNotFoundException("Владелец не может забронировать свою вещь");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking considerationOfBooking(Long bookingId, Long ownerId, boolean available) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemService.getItemById(booking.getItem().getId());
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
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new UserNotFoundException("Бронь с id " + bookingId + " не найдена");
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemService.getItemById(booking.getItem().getId());
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(item.getOwner().getId(), userId)) {
            throw new UserNotFoundException("Только пользователь создавший бронь может просматривать ее");
        }
        return bookingRepository.getReferenceById(bookingId);
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
    public LinkedList<Booking> getAllBookingByOwnerId(Long ownerId, String state, Short from, Short size) {
        isUserExist(ownerId);
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("start").descending());
        if (state == null) {
            return bookingRepository.getAllByItemOwnerId(ownerId, paging);
        }
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
                return bookingRepository.getAllByItemOwnerIdAndApproved(ownerId, BookingStatus.valueOf(state), paging);
            case APPROVED:
            case FUTURE:
            case ALL:
                return bookingRepository.getAllByItemOwnerId(ownerId, paging);
            case CURRENT:
                return bookingRepository.getAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, time, time, paging);
            case PAST:
                return bookingRepository.getAllByItemOwnerIdAndEndBefore(ownerId, time, paging);
            default:
                throw new ValidateException("Something wrong");
        }
    }

    @Override
    public LinkedList<Booking> getAllBookingByUserId(Long userId, String state, Short from, Short size) {
        isUserExist(userId);
        Pageable paging = new OffsetBasedPageRequest(from, size, Sort.by("start").descending());
        if (state == null) {
            return bookingRepository.getAllByBookerId(userId, paging);
        }
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
                return bookingRepository.getAllByBookerIdAndApproved(userId,
                        BookingStatus.valueOf(state), paging);
            case APPROVED:
            case FUTURE:
            case ALL:
                return bookingRepository.getAllByBookerId(userId, paging);
            case CURRENT:
                return bookingRepository.getAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time,
                        paging);
            case PAST:
                return bookingRepository.getAllByBookerIdAndEndBefore(userId, time, paging);
            default:
                throw new ValidateException("Something wrong");
        }
    }

    private void isUserExist(Long id) {
        if (!userService.userIsExistsById(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}