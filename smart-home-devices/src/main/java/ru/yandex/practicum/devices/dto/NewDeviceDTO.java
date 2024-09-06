package ru.yandex.practicum.devices.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NewDeviceDTO {
    private String deviceName;
    private String deviceType;
    private String homeId;
    private Map<String, Object> metadata;
}
