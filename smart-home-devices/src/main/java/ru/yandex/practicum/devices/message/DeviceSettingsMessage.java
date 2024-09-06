package ru.yandex.practicum.devices.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceSettingsMessage {
    private String deviceId;
    private List<DeviceSetting> settings;
    private boolean turnedOn;

    @Data
    @AllArgsConstructor
    public static class DeviceSetting {
        private String settingId;
        private String settingValue;
    }
}
