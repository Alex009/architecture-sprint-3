package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceDetailDTO {
    private String deviceId;
    private String deviceName;
    private String homeId;
    private List<DeviceSettingDTO> settings;
    private List<DeviceSensorDTO> sensors;
}
