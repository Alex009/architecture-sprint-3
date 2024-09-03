package ru.yandex.practicum.telemetry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.message.TelemetryDataMessage;
import ru.yandex.practicum.telemetry.model.SensorData;
import ru.yandex.practicum.telemetry.repository.TelemetryRepository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class KafkaListenerService {

    private final TelemetryRepository telemetryRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaListenerService(TelemetryRepository telemetryRepository,
                                ObjectMapper objectMapper) {
        this.telemetryRepository = telemetryRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "devices.integration.telemetry", groupId = "telemetry-service")
    public void handleTelemetryData(String rawMessage) throws JsonProcessingException {
        TelemetryDataMessage message = objectMapper.readValue(rawMessage, TelemetryDataMessage.class);

        var data = new SensorData(
                OffsetDateTime.parse(message.getTimestamp() + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(),
                message.getSensorId(),
                message.getDeviceId(),
                message.getValue()
        );
        telemetryRepository.save(data);
    }
}
