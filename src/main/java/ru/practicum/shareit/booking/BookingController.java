package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    @PostMapping
    public OutgoingBookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                            @Valid @RequestBody IncomingBookingDto bookingDto) {
        return bookingMapper.bookingToDto(bookingService
                .createBooking(bookingMapper.dtoToBooking(bookerId, bookingDto)));
    }

    @PatchMapping("/{bookingId}")
    public OutgoingBookingDto bookingApproveByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                    @NotNull @PathVariable Long bookingId,
                                                    @NotNull @RequestParam boolean approved) {
        return bookingMapper.bookingToDto(bookingService.considerationOfBooking(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutgoingBookingDto getBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable(required = false) Long bookingId) {
        return bookingMapper.bookingToDto(bookingService.getBookingById(userId, bookingId));

    }

    @GetMapping("/owner")
    public List<OutgoingBookingDto> getAllBookingByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(defaultValue = "0")
                                                           @PositiveOrZero Short from,
                                                           @RequestParam(defaultValue = "32")
                                                           @Positive Short size) {
        return bookingMapper.listBookingToListDto(bookingService.getAllBookingByOwnerId(ownerId, state, from, size));
    }

    @GetMapping
    public List<OutgoingBookingDto> getAllBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0")
                                                          @PositiveOrZero Short from,
                                                          @RequestParam(defaultValue = "32")
                                                          @Positive Short size) {
        return bookingMapper.listBookingToListDto(bookingService.getAllBookingByUserId(userId, state, from, size));
    }
}