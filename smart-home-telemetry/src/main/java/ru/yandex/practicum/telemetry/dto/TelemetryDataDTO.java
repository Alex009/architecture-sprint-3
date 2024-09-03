package ru.yandex.practicum.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TelemetryDataDTO {
    private String sensorId;
    private Instant timestamp;
    private Double value;
}
