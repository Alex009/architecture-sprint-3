package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceSettingDTO {
    private String settingId;
    private String settingName;
    private String settingValue;
}
