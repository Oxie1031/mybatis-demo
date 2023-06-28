package com.example.mybatisdemo.exception;

public class MovieValidationException extends RuntimeException {
    public MovieValidationException(String message) {
        super(message);
    }
}

