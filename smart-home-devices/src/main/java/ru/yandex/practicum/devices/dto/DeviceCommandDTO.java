package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DeviceCommandDTO {

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
