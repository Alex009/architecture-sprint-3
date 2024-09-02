package ru.yandex.practicum.device.heating.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.device.heating.message.DeviceRegistrationFailedMessage;
import ru.yandex.practicum.device.heating.message.DeviceRegistrationSuccessMessage;
import ru.yandex.practicum.device.heating.message.DeviceSettingsMessage;
import ru.yandex.practicum.device.heating.message.TelemetryDataMessage;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDeviceRegisterSuccess(DeviceRegistrationSuccessMessage message) {
        kafkaTemplate.send("devices.integration.register.success", message);
    }

    public void sendDeviceRegisterFailed(DeviceRegistrationFailedMessage message) {
        kafkaTemplate.send("devices.integration.register.failed", message);
    }

    public void sendDeviceSettings(DeviceSettingsMessage message) {
        kafkaTemplate.send("devices.integration.settings", message);
    }

    public void sendTelemetryData(TelemetryDataMessage message) {
        kafkaTemplate.send("devices.integration.telemetry", message);
    }
}
