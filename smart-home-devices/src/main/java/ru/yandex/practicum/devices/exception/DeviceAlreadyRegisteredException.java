package ru.yandex.practicum.devices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeviceAlreadyRegisteredException extends RuntimeException {
    public DeviceAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceAlreadyRegisteredException(String message) {
        super(message);
    }
}
