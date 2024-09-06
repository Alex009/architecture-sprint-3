package ru.yandex.practicum.telemetry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DevicesServiceException extends RuntimeException {
    public DevicesServiceException(String message) {
        super(message);
    }

    public DevicesServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
