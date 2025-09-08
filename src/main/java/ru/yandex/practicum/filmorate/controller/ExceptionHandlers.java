package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConditionsNotMet(final ConditionsNotMetException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleException(final Exception e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatusCode.valueOf(500));
    }
}
