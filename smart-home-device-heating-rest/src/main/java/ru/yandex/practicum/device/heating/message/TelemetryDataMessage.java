package ru.yandex.practicum.device.heating.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryDataMessage {
    private String deviceId;
    private String timestamp;
    private String sensorId;
    private Object data;
}
