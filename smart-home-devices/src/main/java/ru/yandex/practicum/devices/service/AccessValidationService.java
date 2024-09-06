package ru.yandex.practicum.devices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.exception.DeviceNotFoundException;
import ru.yandex.practicum.devices.model.Device;
import ru.yandex.practicum.devices.repository.DeviceRepository;

@Service
public class AccessValidationService {

    private final DeviceRepository deviceRepository;
    private final HomeAccessService homeAccessService;

    @Autowired
    public AccessValidationService(
            DeviceRepository deviceRepository,
            HomeAccessService homeAccessService
    ) {
        this.deviceRepository = deviceRepository;
        this.homeAccessService = homeAccessService;
    }

    public boolean validateAccess(String userId, String deviceId) {
        // Получаем устройство по deviceId
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        String homeId = device.getHomeId();

        return homeAccessService.validateAccess(userId, homeId);
    }
}
