package ru.yandex.practicum.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceAccessRequestDTO {
    private String userId;
    private String deviceId;
}
