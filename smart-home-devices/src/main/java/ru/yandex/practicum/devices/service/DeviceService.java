package ru.yandex.practicum.devices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.dto.*;
import ru.yandex.practicum.devices.exception.AccessDeniedException;
import ru.yandex.practicum.devices.exception.DeviceNotFoundException;
import ru.yandex.practicum.devices.model.Device;
import ru.yandex.practicum.devices.model.DeviceSensor;
import ru.yandex.practicum.devices.model.DeviceSetting;
import ru.yandex.practicum.devices.model.DeviceStatus;
import ru.yandex.practicum.devices.repository.DeviceRepository;
import ru.yandex.practicum.devices.repository.DeviceSensorRepository;
import ru.yandex.practicum.devices.repository.DeviceSettingRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceSettingRepository deviceSettingRepository;
    private final DeviceSensorRepository deviceSensorRepository;
    private final HomeAccessService homeAccessService;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository,
                         DeviceSettingRepository deviceSettingRepository,
                         DeviceSensorRepository deviceSensorRepository,
                         HomeAccessService homeAccessService,
                         KafkaProducerService kafkaProducerService) {
        this.deviceRepository = deviceRepository;
        this.deviceSettingRepository = deviceSettingRepository;
        this.deviceSensorRepository = deviceSensorRepository;
        this.homeAccessService = homeAccessService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<DeviceDTO> getAllDevices(String userId) {
        // Получаем список доступных домов для пользователя
        List<String> accessibleHomeIds = homeAccessService.getAccessibleHomes(userId);

        // Получаем устройства, связанные с этими домами
        List<Device> devices = deviceRepository.findByHomeIdIn(accessibleHomeIds);

        // Преобразуем сущности устройств в DTO и возвращаем список
        return devices.stream()
                .map(this::convertToDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO registerDevice(String userId, NewDeviceDTO newDeviceDTO) {
        // Проверяем доступ пользователя к дому
        if (!homeAccessService.validateAccess(userId, newDeviceDTO.getHomeId())) {
            throw new AccessDeniedException("User does not have access to this home.");
        }

        // Создаем и сохраняем устройство со статусом REGISTRATION
        Device device = new Device();
        device.setDeviceName(newDeviceDTO.getDeviceName());
        device.setHomeId(newDeviceDTO.getHomeId());
        device.setStatus(DeviceStatus.REGISTRATION);
        device.setDeviceId(UUID.randomUUID().toString());
        deviceRepository.save(device);

        // Отправляем сообщение в Kafka о необходимости регистрации устройства
        kafkaProducerService.sendRegisterDevice(device);

        // Вернуть информацию о созданном устройстве
        return convertToDeviceDTO(device);
    }

    public DeviceDetailDTO getDeviceById(String userId, String deviceId) {
        Device device = getAccessedDevice(userId, deviceId);

        List<DeviceSettingDTO> settings = deviceSettingRepository.findByDeviceId(deviceId).stream()
                .map(this::convertToDeviceSettingDTO)
                .collect(Collectors.toList());

        List<DeviceSensorDTO> sensors = deviceSensorRepository.findByDeviceId(deviceId).stream()
                .map(this::convertToDeviceSensorDTO)
                .collect(Collectors.toList());

        return new DeviceDetailDTO(device.getDeviceId(), device.getDeviceName(), device.getHomeId(), settings, sensors);
    }

    public void deleteDevice(String userId, String deviceId) {
        Device device = getAccessedDevice(userId, deviceId);

        deviceRepository.delete(device);

        // Отправляем сообщение в Kafka о том, что устройство было удалено
        kafkaProducerService.sendDeleteDevice(device.getDeviceId());
    }

    public List<DeviceSettingDTO> getDeviceSettings(String userId, String deviceId) {
        Device device = getAccessedDevice(userId, deviceId);

        return deviceSettingRepository.findByDeviceId(device.getDeviceId()).stream()
                .map(this::convertToDeviceSettingDTO)
                .collect(Collectors.toList());
    }

    public void updateDeviceSetting(String userId, String deviceId, UpdateDeviceSettingDTO updateDeviceSettingDTO) {
        Device device = getAccessedDevice(userId, deviceId);

        // Отправляем сообщение в Kafka для обновления настройки устройства
        kafkaProducerService.sendUpdateDeviceSetting(device.getDeviceId(), updateDeviceSettingDTO);
    }

    public void sendDeviceCommand(String userId, String deviceId, DeviceCommandDTO deviceCommandDTO) {
        Device device = getAccessedDevice(userId, deviceId);

        // Отправляем команду устройству через Kafka
        kafkaProducerService.sendDeviceCommand(device.getDeviceId(), deviceCommandDTO);
    }

    private Device getAccessedDevice(String userId, String deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("Device " + deviceId + " not found."));

        if (!homeAccessService.validateAccess(userId, device.getHomeId())) {
            throw new AccessDeniedException("User does not have access.");
        }

        return device;
    }

    private DeviceDTO convertToDeviceDTO(Device device) {
        return new DeviceDTO(device.getDeviceId(), device.getDeviceName(), device.getHomeId(), device.getStatus());
    }

    private DeviceSettingDTO convertToDeviceSettingDTO(DeviceSetting deviceSetting) {
        return new DeviceSettingDTO(
                deviceSetting.getSettingId(),
                deviceSetting.getSettingName(),
                deviceSetting.getSettingValue()
        );
    }

    private DeviceSensorDTO convertToDeviceSensorDTO(DeviceSensor deviceSensor) {
        return new DeviceSensorDTO(
                deviceSensor.getSensorId(),
                deviceSensor.getSensorName(),
                deviceSensor.getSensorType()
        );
    }
}
