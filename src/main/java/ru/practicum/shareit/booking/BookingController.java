package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.ValidateException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    @PostMapping
    public OutgoingBookingDto createBooking(@Valid @RequestHeader(USER_ID_HEADER) Long bookerId,
                                            @Valid @RequestBody IncomingBookingDto bookingDto) {
        return bookingMapper.bookingToDto(bookingService
                .createBooking(bookingMapper.dtoToBooking(bookerId, bookingDto)));
    }

    @PatchMapping({"/{bookingId}"})
    public OutgoingBookingDto bookingApproveByOwner(@Valid @NotNull @RequestHeader(USER_ID_HEADER) Long ownerId,
                                                    @Valid @NotNull @PathVariable Long bookingId,
                                                    @Valid @NotNull @RequestParam boolean approved) {
        return bookingMapper.bookingToDto(bookingService.considerationOfBooking(bookingId, ownerId, approved));
    }

    @GetMapping({"/{bookingId}"})
    public OutgoingBookingDto getBookingByUserId(@Valid @NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable(required = false) Long bookingId) {
        return bookingMapper.bookingToDto(bookingService.getBookingById(userId, bookingId));

    }

    @GetMapping({"/owner"})
    public List<OutgoingBookingDto> getAllBookingByOwnerId(@Valid @NotNull @RequestHeader(USER_ID_HEADER) Long ownerId,
                                                           @RequestParam(required = false) String state) {
        if (state == null) {
            return bookingMapper.listItemToListDto(bookingService.getBookingsByOwner(ownerId));
        }
        List<String> enumNames = Stream.of(Status.values())
                .map(Status::name)
                .collect(Collectors.toList());
        if (!enumNames.contains(state)) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        switch (Status.valueOf(state)) {
            case WAITING:
            case REJECTED:
                return bookingMapper.listItemToListDto(bookingService.getBookingsByOwnerAndState(ownerId,
                        BookingStatus.valueOf(state)));
            case APPROVED:
            case FUTURE:
            case ALL:
                return bookingMapper.listItemToListDto(bookingService.getBookingsByOwner(ownerId));
            case CURRENT:
                return bookingMapper.listItemToListDto(bookingService.getCurrentBookingsByOwnerId(ownerId,
                        LocalDateTime.now()));
            case PAST:
                return bookingMapper.listItemToListDto(bookingService.getPastBookingsByOwnerId(ownerId,
                        LocalDateTime.now()));
            default:
                throw new ValidateException("Something wrong");
        }
    }

    @GetMapping
    public List<OutgoingBookingDto> getAllBookingByUserId(@Valid @NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam(required = false) String state) {
        if (state == null) {
            return bookingMapper.listItemToListDto(bookingService.getBookingsByBooker(userId));
        }
        List<String> enumNames = Stream.of(Status.values())
                .map(Status::name)
                .collect(Collectors.toList());
        if (!enumNames.contains(state)) {
            throw new IncorrectParameterException("Unknown state: " + state);
        }
        switch (Status.valueOf(state)) {
            case WAITING:
            case REJECTED:
                return bookingMapper.listItemToListDto(bookingService.getBookingsByBookerAndState(userId,
                        BookingStatus.valueOf(state)));
            case APPROVED:
            case FUTURE:
            case ALL:
                return bookingMapper.listItemToListDto(bookingService.getBookingsByBooker(userId));
            case CURRENT:
                return bookingMapper.listItemToListDto(bookingService.getCurrentBookingsByBooker(userId,
                        LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                return bookingMapper.listItemToListDto(bookingService.getPastBookingsByBooker(userId,
                        LocalDateTime.now()));
            default:
                throw new ValidateException("Something wrong");
        }
    }
}