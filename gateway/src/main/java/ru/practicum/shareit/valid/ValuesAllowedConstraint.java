package ru.practicum.shareit.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Constraint(validatedBy = ValuesAllowedConstraintValidator.class)
public @interface ValuesAllowedConstraint {

    String message() default "{value.hasWrong}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String propName();
    String[] values();
}