package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void validateExceptionTest() {
        ValidateException validateException = new ValidateException("Неверный аргумент");
        ErrorResponse result = errorHandler.handleArgumentNotValidException(validateException);
        assertEquals("Неверный аргумент", result.getError());
    }

    @Test
    void incorrectParameterExceptionTest() {
        IncorrectParameterException incorrectParameterException = new IncorrectParameterException("Не найден");
        ErrorResponse result = errorHandler.handleIncorrectParameterException(incorrectParameterException);
        System.out.println(result.toString());
        assertEquals("Не найден", result.getError());
    }

    @Test
    void emailAlreadyExistExceptionTest() {
        EmailAlreadyExistException emailAlreadyExistException = new EmailAlreadyExistException("Почта уже существует");
        ErrorResponse result = errorHandler.handleEmailAlreadyExistException(emailAlreadyExistException);
        System.out.println(result.toString());
        assertEquals("Почта уже существует", result.getError());
    }

    @Test
    void userNotFoundExceptionTest() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("Пользователь не найден");
        ErrorResponse result = errorHandler.handleUserNotFoundException(userNotFoundException);
        System.out.println(result.toString());
        assertEquals("Пользователь не найден", result.getError());
    }

    @Test
    void itemNotFoundExceptionTest() {
        ItemNotFoundException itemNotFoundException = new ItemNotFoundException("Вещь не найдена");
        ErrorResponse result = errorHandler.handleItemNotFoundException(itemNotFoundException);
        System.out.println(result.toString());
        assertEquals("Вещь не найдена", result.getError());
    }

    @Test
    void throwableTest() {
        Throwable throwable = new Throwable("Произошла непредвиденная ошибка.");
        ErrorResponse result = errorHandler.handleThrowable(throwable);
        System.out.println(result.toString());
        assertEquals("Произошла непредвиденная ошибка.", result.getError());
    }
}