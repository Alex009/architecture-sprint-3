package ru.yandex.practicum.devices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.devices.model.DeviceSensor;

import java.util.List;

@Repository
public interface DeviceSensorRepository extends JpaRepository<DeviceSensor, String> {

    // Найти все сенсоры, связанные с конкретным устройством
    List<DeviceSensor> findByDeviceId(String deviceId);
}
