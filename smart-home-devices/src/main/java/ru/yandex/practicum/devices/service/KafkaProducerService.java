package ru.yandex.practicum.devices.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Data
    @AllArgsConstructor
    public static class RegisterDeviceMessage {
        private String deviceId;
        private String deviceType;
        private Map<String, Object> metadata;
    }

    @Data
    @AllArgsConstructor
    public static class DeleteDeviceMessage {
        private String deviceId;
    }

    @Data
    @AllArgsConstructor
    public static class DeviceCommandMessage {
        private String deviceId;
        private CommandType command;
        private Map<String, Object> parameters;

        public enum CommandType {
            TURN_ON("turn-on"),
            TURN_OFF("turn-off"),
            CHANGE_SETTING("change-setting"),
            READ_SETTINGS("read-settings");

            private final String value;

            CommandType(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return value;
            }
        }
    }
}
