package ru.yandex.practicum.devices.dto;

import lombok.Data;

@Data
public class UpdateDeviceSettingDTO {
    private String settingId;
    private String settingValue;
}
