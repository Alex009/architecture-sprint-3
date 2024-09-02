package ru.yandex.practicum.device.heating.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.device.heating.message.*;
import ru.yandex.practicum.device.heating.model.Device;
import ru.yandex.practicum.device.heating.repository.DeviceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class KafkaListenerService {
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public KafkaListenerService(DeviceRepository deviceRepository,
                                ObjectMapper objectMapper,
                                KafkaProducerService kafkaProducerService) {
        this.deviceRepository = deviceRepository;
        this.objectMapper = objectMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "devices.integration.register.start", groupId = "device-heating-rest")
    public void handleRegistration(String rawMessage) throws JsonProcessingException {
        RegisterDeviceMessage message = objectMapper.readValue(rawMessage, RegisterDeviceMessage.class);
        if (!message.getDeviceType().contentEquals("heating-rest")) return;

        // псевдо регистрация девайса
        try {
            Device device = new Device(
                    message.getDeviceId(),
                    message.getMetadata().get("access-url").toString()
            );
            deviceRepository.save(device);

            var response = new DeviceRegistrationSuccessMessage(
                    device.getDeviceId(),
                    "heating system",
                    List.of(
                            new DeviceRegistrationSuccessMessage.DeviceSetting(
                                    "target-temperature",
                                    "Целевая температура",
                                    "30"
                            )
                    ),
                    List.of(
                            new DeviceRegistrationSuccessMessage.DeviceSensor(
                                    "current-temperature",
                                    "Текущая температура",
                                    "temperature"
                            )
                    )
            );
            kafkaProducerService.sendDeviceRegisterSuccess(response);
        } catch (Exception e) {
            var response = new DeviceRegistrationFailedMessage(
                    message.getDeviceId(),
                    e.getMessage(),
                    -9
            );
            kafkaProducerService.sendDeviceRegisterFailed(response);
        }
    }

    @KafkaListener(topics = "devices.integration.delete", groupId = "device-heating-rest")
    public void handleDeletion(String rawMessage) throws JsonProcessingException {
        DeleteDeviceMessage message = objectMapper.readValue(rawMessage, DeleteDeviceMessage.class);
        deviceRepository.deleteById(message.getDeviceId());
    }

    @KafkaListener(topics = "devices.integration.command.run", groupId = "device-heating-rest")
    public void handleCommand(String rawMessage) throws JsonProcessingException {
        DeviceCommandMessage message = objectMapper.readValue(rawMessage, DeviceCommandMessage.class);
        Optional<Device> result = deviceRepository.findById(message.getDeviceId());
        if (result.isEmpty()) return; // команда не к нам

        Device device = result.get();
        DeviceSettingsMessage settings;

        switch (message.getCommand()) {
            case TURN_ON -> {
                var url = device.getAccessUrl();
                // типа включили
                settings = new DeviceSettingsMessage(
                        device.getDeviceId(),
                        List.of(
                                new DeviceSettingsMessage.DeviceSetting("target-temperature", "30")
                        ),
                        true
                );
            }
            case TURN_OFF -> {
                var url = device.getAccessUrl();
                // типа выключили
                settings = new DeviceSettingsMessage(
                        device.getDeviceId(),
                        List.of(
                                new DeviceSettingsMessage.DeviceSetting("target-temperature", "30")
                        ),
                        false
                );
            }
            case CHANGE_SETTING -> {
                var url = device.getAccessUrl();
                // типа изменили настройки
                settings = new DeviceSettingsMessage(
                        device.getDeviceId(),
                        List.of(
                                new DeviceSettingsMessage.DeviceSetting("target-temperature", message.getParameters().get("target-temperature").toString())
                        ),
                        true
                );
            }
            case READ_SETTINGS -> {
                var url = device.getAccessUrl();
                // типа получили новые настройки
                settings = new DeviceSettingsMessage(
                        device.getDeviceId(),
                        List.of(
                                new DeviceSettingsMessage.DeviceSetting("target-temperature", "30")
                        ),
                        true
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + message.getCommand());
        }

        kafkaProducerService.sendDeviceSettings(settings);
    }
}
