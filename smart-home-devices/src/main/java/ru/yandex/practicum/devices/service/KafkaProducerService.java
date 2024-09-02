package ru.yandex.practicum.devices.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.dto.DeviceCommandDTO;
import ru.yandex.practicum.devices.dto.UpdateDeviceSettingDTO;
import ru.yandex.practicum.devices.model.Device;

import java.util.Map;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRegisterDevice(Device device) {
        RegisterDeviceMessage message = new RegisterDeviceMessage(device.getDeviceId(), device.getDeviceName(), device.getHomeId());
        kafkaTemplate.send("devices.integration.register.start", message);
    }

    public void sendDeleteDevice(String deviceId) {
        kafkaTemplate.send("devices.deletion", deviceId);
    }

    public void sendUpdateDeviceSetting(String deviceId, UpdateDeviceSettingDTO updateDeviceSettingDTO) {
//        kafkaTemplate.send("devices.update-setting", new DeviceSettingUpdateMessage(deviceId, updateDeviceSettingDTO));
    }

    public void sendDeviceCommand(String deviceId, DeviceCommandDTO deviceCommandDTO) {
//        kafkaTemplate.send("devices.integration.command.run", new DeviceCommandMessage(
//                deviceId,
//                deviceCommandDTO.getCommandId(),
//                deviceCommandDTO.getCommand(),
//                deviceCommandDTO.getParameters()
//        ));
    }

    @Data
    @AllArgsConstructor
    private static class RegisterDeviceMessage {
        private String deviceId;
        private String deviceName;
        private String homeId;
    }

    @Data
    @AllArgsConstructor
    public class DeviceCommandMessage {

        private String deviceId;
        private String commandId;
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
