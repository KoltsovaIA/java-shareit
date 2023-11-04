package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutgoingBookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                            @RequestBody IncomingBookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public OutgoingBookingDto bookingApproveByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam Boolean approved) {
        return bookingService.considerationOfBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutgoingBookingDto getBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable(required = false) Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);

    }

    @GetMapping("/owner")
    public List<OutgoingBookingDto> getAllBookingByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(defaultValue = "0") Short from,
                                                           @RequestParam(defaultValue = "32") Short size) {
        return bookingService.getAllBookingByOwnerId(ownerId, state, from, size);
    }

    @GetMapping
    public List<OutgoingBookingDto> getAllBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") Short from,
                                                          @RequestParam(defaultValue = "20") Short size) {
        return bookingService.getAllBookingByUserId(userId, state, from, size);
    }
}