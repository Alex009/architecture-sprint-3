package ru.yandex.practicum.telemetry.repository;

import ru.yandex.practicum.telemetry.model.SensorData;

import java.time.Instant;
import java.util.List;

public interface TelemetryRepository {
    void save(SensorData sensorData);

    SensorData findLatestSensorDataByDeviceId(String deviceId);

    List<SensorData> findSensorDataByDeviceIdAndTimeRange(String deviceId, Instant startDate, Instant endDate);
}
