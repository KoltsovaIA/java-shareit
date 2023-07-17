package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    @PostMapping
    public OutgoingBookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                            @Valid @RequestBody IncomingBookingDto bookingDto) {
        return bookingMapper.bookingToDto(bookingService
                .createBooking(bookingMapper.dtoToBooking(bookerId, bookingDto)));
    }

    @PatchMapping("/{bookingId}")
    public OutgoingBookingDto bookingApproveByOwner(@NotNull @RequestHeader(USER_ID_HEADER) Long ownerId,
                                                    @NotNull @PathVariable Long bookingId,
                                                    @NotNull @RequestParam boolean approved) {
        return bookingMapper.bookingToDto(bookingService.considerationOfBooking(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutgoingBookingDto getBookingByUserId(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable(required = false) Long bookingId) {
        return bookingMapper.bookingToDto(bookingService.getBookingById(userId, bookingId));

    }

    @GetMapping("/owner")
    public List<OutgoingBookingDto> getAllBookingByOwnerId(@NotNull @RequestHeader(USER_ID_HEADER) Long ownerId,
                                                           @RequestParam(required = false) String state) {
        return bookingMapper.listItemToListDto(bookingService.getAllBookingByOwnerId(ownerId, state));
    }

    @GetMapping
    public List<OutgoingBookingDto> getAllBookingByUserId(@NotNull @RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam(required = false) String state) {
        return bookingMapper.listItemToListDto(bookingService.getAllBookingByUserId(userId, state));
    }
}