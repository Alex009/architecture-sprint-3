package ru.yandex.practicum.devices.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "devices")
@Data
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "home_id", nullable = false)
    private String homeId;
}
