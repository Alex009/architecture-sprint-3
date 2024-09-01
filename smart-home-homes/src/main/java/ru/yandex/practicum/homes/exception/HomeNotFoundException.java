package ru.yandex.practicum.homes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HomeNotFoundException extends RuntimeException {

    public HomeNotFoundException(String message) {
        super(message);
    }
}
