package ru.yandex.practicum.device.heating.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceRegistrationFailedMessage {
    private String deviceId;
    private String errorMessage;
    private int errorCode;
}
