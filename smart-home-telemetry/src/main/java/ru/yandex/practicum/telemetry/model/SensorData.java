package ru.yandex.practicum.telemetry.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = "sensor_data")
public class SensorData {

    @Column(name = "time")
    private Instant timestamp;

    @Column(name = "sensor_id", tag = true)
    private String sensorId;

    @Column(name = "device_id", tag = true)
    private String deviceId;

    @Column(name = "value")
    private Double value;
}
