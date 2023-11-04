package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.valid.StartBeforeEndDateValid;
import ru.practicum.shareit.valid.ValuesAllowedConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                @RequestBody
                                                @Valid
                                                @StartBeforeEndDateValid(message =
                                                        "Дата окончания не может быть раньше или равна дате начала")
                                                IncomingBookingDto incomingBookingDto) {
        return bookingClient.createBooking(bookerId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApproveByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                        @NotNull @PathVariable Long bookingId,
                                                        @NotNull @RequestParam Boolean approved) {
        return bookingClient.bookingApproveByOwner(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable(required = false) Long bookingId) {
        return bookingClient.getBookingByUserId(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                         @ValuesAllowedConstraint(propName = "state",
                                                                 values = {"all",
                                                                         "current",
                                                                         "past",
                                                                         "future",
                                                                         "waiting",
                                                                         "rejected"},
                                                                 message = "Unknown state: UNSUPPORTED_STATUS")
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                                         @RequestParam(defaultValue = "32") @Min(1) @Max(32) int size) {
        return bookingClient.getAllBookingByOwnerId(ownerId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @ValuesAllowedConstraint(propName = "state",
                                                                values = {"all",
                                                                        "current",
                                                                        "past",
                                                                        "future",
                                                                        "waiting",
                                                                        "rejected"},
                                                                message = "Unknown state: UNSUPPORTED_STATUS")
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0)
                                                        @Max(Integer.MAX_VALUE) int from,
                                                        @RequestParam(defaultValue = "20") @Min(1) @Max(32) int size) {
        return bookingClient.getAllBookingByUserId(userId, state, from, size);
    }

}