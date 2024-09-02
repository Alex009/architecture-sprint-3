package ru.yandex.practicum.devices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.exception.DeviceNotFoundException;
import ru.yandex.practicum.devices.model.Device;
import ru.yandex.practicum.devices.model.DeviceSensor;
import ru.yandex.practicum.devices.model.DeviceSetting;
import ru.yandex.practicum.devices.model.DeviceStatus;
import ru.yandex.practicum.devices.repository.DeviceRepository;
import ru.yandex.practicum.devices.repository.DeviceSensorRepository;
import ru.yandex.practicum.devices.repository.DeviceSettingRepository;

import java.util.List;

@Service
public class KafkaListenerService {

    private final DeviceRepository deviceRepository;
    private final DeviceSettingRepository deviceSettingRepository;
    private final DeviceSensorRepository deviceSensorRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaListenerService(DeviceRepository deviceRepository,
                                DeviceSettingRepository deviceSettingRepository,
                                DeviceSensorRepository deviceSensorRepository,
                                ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.deviceSettingRepository = deviceSettingRepository;
        this.deviceSensorRepository = deviceSensorRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "devices.integration.register.success", groupId = "devices-service")
    public void handleRegistrationSuccess(DeviceRegistrationSuccessMessage message) {
        Device device = deviceRepository.findById(message.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + message.getDeviceId()));
        device.setStatus(DeviceStatus.REGISTERED);
        device.setDeviceName(message.getName());
        deviceRepository.save(device);

        // Сохранение всех settings в базу данных
        for (DeviceRegistrationSuccessMessage.DeviceSetting setting : message.getSettings()) {
            DeviceSetting deviceSetting = new DeviceSetting();
            deviceSetting.setSettingId(setting.getSettingId());
            deviceSetting.setSettingName(setting.getSettingName());
            deviceSetting.setSettingValue(setting.getSettingValue());
            deviceSetting.setDeviceId(device.getDeviceId());
            deviceSettingRepository.save(deviceSetting);
        }

        // Сохранение всех sensors в базу данных
        for (DeviceRegistrationSuccessMessage.DeviceSensor sensor : message.getSensors()) {
            DeviceSensor deviceSensor = new DeviceSensor();
            deviceSensor.setSensorId(sensor.getSensorId());
            deviceSensor.setSensorName(sensor.getSensorName());
            deviceSensor.setDeviceId(device.getDeviceId());
            deviceSensorRepository.save(deviceSensor);
        }
    }

    @KafkaListener(topics = "devices.integration.register.failed", groupId = "devices-service")
    public void handleRegistrationFailed(DeviceRegistrationFailedMessage message) {
        Device device = deviceRepository.findById(message.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + message.getDeviceId()));
        device.setStatus(DeviceStatus.REGISTRATION_FAILED);

        var details = new Device.StatusErrorDetails(
                message.errorMessage,
                message.errorCode
        );

        try {
            // Записать details в виде json строки
            String jsonDetails = objectMapper.writeValueAsString(details);
            device.setStatusDetails(jsonDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize error details", e);
        }

        deviceRepository.save(device);
    }

    @Data
    public static class DeviceRegistrationSuccessMessage {
        private String deviceId;
        private String name;
        private List<DeviceSetting> settings;
        private List<DeviceSensor> sensors;

        @Data
        public static class DeviceSetting {
            private String settingId;
            private String settingName;
            private String settingValue;
        }

        @Data
        public static class DeviceSensor {
            private String sensorId;
            private String sensorName;
        }
    }

    @Data
    public static class DeviceRegistrationFailedMessage {
        private String deviceId;
        private String errorMessage;
        private int errorCode;
    }
}
