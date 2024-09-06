package ru.yandex.practicum.device.heating.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.device.heating.model.Device;
import ru.yandex.practicum.device.heating.repository.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DeviceRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Device device) {
        redisTemplate.opsForValue().setIfAbsent(device.getDeviceId(), device);
    }

    @Override
    public Optional<Device> findById(String deviceId) {
        Device device = (Device) redisTemplate.opsForValue().get(deviceId);
        return Optional.ofNullable(device);
    }

    @Override
    public List<Device> findAll() {
        return redisTemplate.keys("*").stream()
                .map(id -> (Device) redisTemplate.opsForValue().get(id))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String deviceId) {
        redisTemplate.opsForValue().getAndDelete(deviceId);
    }
}
