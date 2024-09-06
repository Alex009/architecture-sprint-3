package ru.yandex.practicum.devices.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "device_settings")
@Data
public class DeviceSetting {

    @Id
    @Column(name = "setting_id", nullable = false)
    private String settingId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "setting_name", nullable = false)
    private String settingName;

    @Column(name = "setting_value")
    private String settingValue;
}
