package ru.yandex.practicum.device.heating.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Device {
    private String deviceId;
    private String accessUrl;
}
