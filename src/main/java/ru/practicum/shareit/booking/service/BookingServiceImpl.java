package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        Long bookerId = booking.getBookerId();
        Long itemId = booking.getItemId();
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("пользователь id " + bookerId + " не найден !");
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
        if (bookerId == itemService.getItemById(itemId).getOwner()) {
            throw new ItemNotFoundException("Владелец не может забронировать свою вещь");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking considerationOfBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemService.getItemById(booking.getItemId());
        if (item.getOwner() != ownerId) {
            throw new UserNotFoundException("изменить состояние бронирования может только владелец вещи");
        }
        if (approved && booking.getApproved() != BookingStatus.APPROVED) {
            booking.setApproved(BookingStatus.APPROVED);
        } else if (!approved && booking.getApproved() != BookingStatus.REJECTED) {
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
        Item item = itemService.getItemById(booking.getItemId());
        if (!Objects.equals(booking.getBookerId(), userId) && !Objects.equals(item.getOwner(), userId)) {
            throw new UserNotFoundException("Только пользователь создавший бронь может просматривать ее");
        }
        return bookingRepository.getReferenceById(bookingId);
    }

    @Override
    public LinkedList<Booking> getBookingsByBooker(Long bookerId) {
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("пользователь с id " + bookerId + " не найден ");
        }
        return bookingRepository.getAllByBookerIdOrderByStartDesc(bookerId);
    }

    @Override
    public LinkedList<Booking> getBookingsByBookerAndState(Long bookerId, BookingStatus state) {
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("Пользователь id " + bookerId + " не найден!");
        }
        return bookingRepository.getAllByBookerIdAndApprovedOrderByStartDesc(bookerId, state);
    }

    @Override
    public LinkedList<Booking> getCurrentBookingsByBooker(Long bookerId, LocalDateTime start,
                                                          LocalDateTime end) {
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("Пользователь id " + bookerId + " не найден!");
        }
        return bookingRepository.getAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                start, end);
    }

    @Override
    public LinkedList<Booking> getPastBookingsByBooker(Long bookerId, LocalDateTime end) {
        if (!userService.userIsExistsById(bookerId)) {
            throw new UserNotFoundException("Пользователь с id- " + bookerId + " не найден.");
        }
        return bookingRepository.getAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, end);
    }

    @Override
    public LinkedList<Booking> getBookingsByOwner(Long ownerId) {
        if (!userService.userIsExistsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id : " + ownerId + " не найден!");
        }
        return bookingRepository.getAllByOwner(ownerId);
    }

    @Override
    public LinkedList<Booking> getBookingsByOwnerAndState(Long ownerId, BookingStatus state) {
        if (!userService.userIsExistsById(ownerId)) {
            throw new UserNotFoundException("Пользователь id " + ownerId + " не найден.");
        }
        return bookingRepository.getAllByOwnerAndState(ownerId, state.name());
    }

    @Override
    public LinkedList<Booking> getPastBookingsByOwnerId(Long ownerId, LocalDateTime time) {
        if (!userService.userIsExistsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        return bookingRepository.getPastAllByOwnerId(ownerId, time);
    }

    @Override
    public LinkedList<Booking> getCurrentBookingsByOwnerId(Long ownerId, LocalDateTime time) {
        if (!userService.userIsExistsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        return bookingRepository.getCurrentAllByOwnerId(ownerId, LocalDateTime.now());
    }

    @Override
    public Booking findLastBooking(Long itemId, LocalDateTime start) {
        return bookingRepository.findLastBooking(itemId, start);
    }

    @Override
    public Booking findNextBooking(Long itemId, LocalDateTime start) {
        return bookingRepository.findNextBooking(itemId, start);
    }
}