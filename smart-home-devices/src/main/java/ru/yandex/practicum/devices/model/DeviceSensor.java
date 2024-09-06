package ru.yandex.practicum.devices.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "device_sensors")
@Data
public class DeviceSensor {

    @Id
    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "sensor_name", nullable = false)
    private String sensorName;

    @Column(name = "sensor_type", nullable = false)
    private String sensorType;
}
