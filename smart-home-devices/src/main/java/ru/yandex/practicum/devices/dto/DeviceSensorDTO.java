package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceSensorDTO {

    private String sensorId;
    private String sensorName;
    private String sensorType;
}
