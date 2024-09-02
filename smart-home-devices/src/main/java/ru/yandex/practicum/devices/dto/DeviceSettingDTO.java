package ru.yandex.practicum.devices.dto;

import lombok.Data;

@Data
public class DeviceSettingDTO {
    private String settingId;
    private String settingName;
    private String settingValue;
}
