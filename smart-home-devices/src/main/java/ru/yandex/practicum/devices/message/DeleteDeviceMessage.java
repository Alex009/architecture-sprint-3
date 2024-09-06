package ru.yandex.practicum.devices.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteDeviceMessage {
    private String deviceId;
}
