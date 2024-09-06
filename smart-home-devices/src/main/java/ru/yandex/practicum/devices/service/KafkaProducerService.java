package ru.yandex.practicum.devices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.message.DeleteDeviceMessage;
import ru.yandex.practicum.devices.message.DeviceCommandMessage;
import ru.yandex.practicum.devices.message.RegisterDeviceMessage;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRegisterDevice(RegisterDeviceMessage message) {
        kafkaTemplate.send("devices.integration.register.start", message);
    }

    public void sendDeleteDevice(DeleteDeviceMessage message) {
        kafkaTemplate.send("devices.integration.delete", message);
    }

    public void sendDeviceCommand(DeviceCommandMessage message) {
        kafkaTemplate.send("devices.integration.command.run", message);
    }
}
