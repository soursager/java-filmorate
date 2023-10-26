package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.Map;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectValidation(final ValidationException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }

    @ExceptionHandler({DataNotFoundException.class, NotFoundLikeException.class, DataIsNullException.class,
            FriendOnTheListException.class, NegativeNumberException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleIncorrectCount(final RuntimeException e) {
        return Map.of(
                "error", "Ошибка входящих данных",
                "errorMessage", e.getMessage()
        );
    }
}
