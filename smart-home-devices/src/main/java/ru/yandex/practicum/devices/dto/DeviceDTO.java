package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.devices.model.DeviceStatus;

@Data
@AllArgsConstructor
public class DeviceDTO {
    private String deviceId;
    private String deviceName;
    private String homeId;
    private DeviceStatus status;
}
