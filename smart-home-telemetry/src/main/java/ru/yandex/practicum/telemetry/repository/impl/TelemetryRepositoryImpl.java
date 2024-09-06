package ru.yandex.practicum.telemetry.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.model.SensorData;
import ru.yandex.practicum.telemetry.repository.TelemetryRepository;

import java.time.Instant;
import java.util.List;

@Repository
public class TelemetryRepositoryImpl implements TelemetryRepository {

    private final InfluxDBClient influxDBClient;
    private final WriteApi writeApi;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Autowired
    public TelemetryRepositoryImpl(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
        this.writeApi = influxDBClient.makeWriteApi();
    }

    @Override
    public void save(SensorData sensorData) {
        writeApi.writeMeasurement(WritePrecision.MS, sensorData);
    }

    @Override
    public SensorData findLatestSensorDataByDeviceId(String deviceId) {
        String query = String.format(
                "from(bucket: \"%s\") |> range(start: -1h) |> filter(fn: (r) => r[\"device_id\"] == \"%s\") |> sort(columns:[\"_time\"], desc: true) |> limit(n:1)",
                bucket, deviceId
        );

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<SensorData> results = queryApi.query(query, SensorData.class);

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<SensorData> findSensorDataByDeviceIdAndTimeRange(String deviceId, Instant startDate, Instant endDate) {
        String query = String.format(
                "from(bucket: \"%s\") |> range(start: %s, stop: %s) |> filter(fn: (r) => r[\"device_id\"] == \"%s\") |> sort(columns:[\"_time\"], desc: true)",
                bucket,
                startDate.toString(),
                endDate.toString(),
                deviceId
        );

        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(query, SensorData.class);
    }
}
