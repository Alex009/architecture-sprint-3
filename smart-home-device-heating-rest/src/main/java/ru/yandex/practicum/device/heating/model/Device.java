package ru.yandex.practicum.device.heating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String deviceId;
    private String accessUrl;
}
