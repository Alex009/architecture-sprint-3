package ru.yandex.practicum.device.heating.repository;

import ru.yandex.practicum.device.heating.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    void save(Device device);

    Optional<Device> findById(String deviceId);

    List<Device> findAll();

    void deleteById(String deviceId);
}
