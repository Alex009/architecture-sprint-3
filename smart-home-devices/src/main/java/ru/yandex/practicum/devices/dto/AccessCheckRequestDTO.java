package ru.yandex.practicum.devices.dto;

import lombok.Data;

@Data
public class AccessCheckRequestDTO {
    private String userId;
    private String deviceId;
}
