package ru.yandex.practicum.devices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.devices.model.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findById(String deviceId);

    List<Device> findByHomeIdIn(List<String> homeIds);
}
