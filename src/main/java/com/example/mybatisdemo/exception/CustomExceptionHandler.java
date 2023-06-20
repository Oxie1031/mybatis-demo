package com.example.mybatisdemo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = MovieNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(
            MovieNotFoundException e, HttpServletRequest request) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                "path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("timestamp", ZonedDateTime.now().toString());
        errors.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errors.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errors.put("path", request.getRequestURI());
        return errors;
    }
}
