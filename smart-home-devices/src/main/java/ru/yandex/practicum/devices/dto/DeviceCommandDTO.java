package ru.yandex.practicum.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceCommandDTO {
    private CommandType command;

    public enum CommandType {
        TURN_ON("turn-on"),
        TURN_OFF("turn-off");

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
