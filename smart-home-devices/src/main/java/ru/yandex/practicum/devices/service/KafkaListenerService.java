package ru.yandex.practicum.devices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.exception.DeviceNotFoundException;
import ru.yandex.practicum.devices.message.DeviceRegistrationFailedMessage;
import ru.yandex.practicum.devices.message.DeviceRegistrationSuccessMessage;
import ru.yandex.practicum.devices.model.Device;
import ru.yandex.practicum.devices.model.DeviceSensor;
import ru.yandex.practicum.devices.model.DeviceSetting;
import ru.yandex.practicum.devices.model.DeviceStatus;
import ru.yandex.practicum.devices.repository.DeviceRepository;
import ru.yandex.practicum.devices.repository.DeviceSensorRepository;
import ru.yandex.practicum.devices.repository.DeviceSettingRepository;

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
    public void handleRegistrationSuccess(String rawMessage) throws JsonProcessingException {
        DeviceRegistrationSuccessMessage message = objectMapper.readValue(rawMessage, DeviceRegistrationSuccessMessage.class);
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
            deviceSensor.setSensorType(sensor.getSensorType());
            deviceSensor.setDeviceId(device.getDeviceId());
            deviceSensorRepository.save(deviceSensor);
        }
    }

    @KafkaListener(topics = "devices.integration.register.failed", groupId = "devices-service")
    public void handleRegistrationFailed(String rawMessage) throws JsonProcessingException {
        DeviceRegistrationFailedMessage message = objectMapper.readValue(rawMessage, DeviceRegistrationFailedMessage.class);
        Device device = deviceRepository.findById(message.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + message.getDeviceId()));
        device.setStatus(DeviceStatus.REGISTRATION_FAILED);

        var details = new Device.StatusErrorDetails(
                message.getErrorMessage(),
                message.getErrorCode()
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
}
