package ru.yandex.practicum.device.heating.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RegisterDeviceMessage {
    private String deviceId;
    private String deviceType;
    private Map<String, Object> metadata;
}
