package ru.yandex.practicum.devices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.devices.model.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    // Метод для поиска устройства по ID
    Optional<Device> findById(String deviceId);

    // Метод для поиска устройств по homeId (например, если userId может быть связан с homeId)
    List<Device> findByHomeId(String homeId);
}
