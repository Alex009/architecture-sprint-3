package ru.yandex.practicum.device.heating.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteDeviceMessage {
    private String deviceId;
}
