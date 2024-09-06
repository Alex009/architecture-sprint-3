package ru.yandex.practicum.devices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.devices.model.DeviceSetting;

import java.util.List;

@Repository
public interface DeviceSettingRepository extends JpaRepository<DeviceSetting, String> {

    // Найти все настройки для конкретного устройства
    List<DeviceSetting> findByDeviceId(String deviceId);

    // Найти настройку по deviceId и имени настройки
    DeviceSetting findByDeviceIdAndSettingName(String deviceId, String settingName);
}
