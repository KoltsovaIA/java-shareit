package ru.practicum.shareit.valid;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, IncomingBookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(IncomingBookingDto incomingBookingDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = incomingBookingDto.getStart();
        LocalDateTime end = incomingBookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
