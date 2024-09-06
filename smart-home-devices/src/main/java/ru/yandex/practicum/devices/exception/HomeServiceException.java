package ru.yandex.practicum.devices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HomeServiceException extends RuntimeException {
    public HomeServiceException(String message) {
        super(message);
    }

    public HomeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
