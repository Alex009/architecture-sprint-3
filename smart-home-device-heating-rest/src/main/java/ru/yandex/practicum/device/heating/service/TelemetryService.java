package ru.yandex.practicum.device.heating.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.device.heating.model.Device;
import ru.yandex.practicum.device.heating.message.TelemetryDataMessage;
import ru.yandex.practicum.device.heating.repository.DeviceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TelemetryService {

    private final DeviceRepository deviceRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public TelemetryService(DeviceRepository deviceRepository, KafkaProducerService kafkaProducerService) {
        this.deviceRepository = deviceRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Scheduled(fixedRate = 5000)
    public void sendTelemetryData() {
        // Читаем все устройства из Redis
        List<Device> devices = deviceRepository.findAll();

        // Для каждого устройства отправляем сообщение телеметрии в Kafka
        for (Device device : devices) {
            TelemetryDataMessage telemetryData = new TelemetryDataMessage(
                    device.getDeviceId(),
                    LocalDateTime.now().toString(),
                    "current-temperature",
                    generateFakeTelemetryData()  // Генерация фейковых данных телеметрии
            );
            kafkaProducerService.sendTelemetryData(telemetryData);
        }
    }

    private String generateFakeTelemetryData() {
        // Генерация фейковых данных телеметрии
        // Например, возвращаем случайное значение температуры
        return String.valueOf(20 + Math.random() * 10);
    }
}
