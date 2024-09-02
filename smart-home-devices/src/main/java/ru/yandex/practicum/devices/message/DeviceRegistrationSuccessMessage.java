package ru.yandex.practicum.devices.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceRegistrationSuccessMessage {
    private String deviceId;
    private String name;
    private List<DeviceSetting> settings;
    private List<DeviceSensor> sensors;

    @Data
    @AllArgsConstructor
    public static class DeviceSetting {
        private String settingId;
        private String settingName;
        private String settingValue;
    }

    @Data
    @AllArgsConstructor
    public static class DeviceSensor {
        private String sensorId;
        private String sensorName;
        private String sensorType;
    }
}
