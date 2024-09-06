package ru.yandex.practicum.telemetry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.dto.TelemetryDataDTO;
import ru.yandex.practicum.telemetry.exception.AccessDeniedException;
import ru.yandex.practicum.telemetry.exception.DataNotFoundException;
import ru.yandex.practicum.telemetry.model.SensorData;
import ru.yandex.practicum.telemetry.repository.TelemetryRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelemetryService {

    private final DeviceAccessService deviceAccessService;
    private final TelemetryRepository telemetryRepository;

    @Autowired
    public TelemetryService(DeviceAccessService deviceAccessService, TelemetryRepository telemetryRepository) {
        this.deviceAccessService = deviceAccessService;
        this.telemetryRepository = telemetryRepository;
    }

    public TelemetryDataDTO getLatestTelemetry(String deviceId, String userId) {
        if (!deviceAccessService.validateAccess(userId, deviceId)) {
            throw new AccessDeniedException("Access denied");
        }

        SensorData latestSensorData = telemetryRepository.findLatestSensorDataByDeviceId(deviceId);

        if (latestSensorData == null) {
            throw new DataNotFoundException();
        }

        return new TelemetryDataDTO(
                latestSensorData.getSensorId(),
                latestSensorData.getTimestamp(),
                latestSensorData.getValue()
        );
    }

    public List<TelemetryDataDTO> getHistoricalTelemetry(String deviceId, Instant startDate, Instant endDate, String userId) {
        if (!deviceAccessService.validateAccess(userId, deviceId)) {
            throw new AccessDeniedException("Access denied");
        }

        List<SensorData> sensorDataList = telemetryRepository.findSensorDataByDeviceIdAndTimeRange(deviceId, startDate, endDate);

        // Преобразуем список SensorData в список TelemetryDataDTO
        return sensorDataList.stream()
                .map(sensorData -> new TelemetryDataDTO(
                        sensorData.getSensorId(),
                        sensorData.getTimestamp(),
                        sensorData.getValue()
                ))
                .collect(Collectors.toList());
    }
}
