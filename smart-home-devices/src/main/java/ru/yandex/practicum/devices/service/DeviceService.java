package ru.yandex.practicum.devices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.devices.dto.*;
import ru.yandex.practicum.devices.repository.DeviceRepository;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    //    private final DeviceSettingRepository deviceSettingRepository;
//    private final DeviceCommandRepository deviceCommandRepository;
    private final HomeAccessService homeAccessService; // Assume this is a client for calling homes microservice

    @Autowired
    public DeviceService(DeviceRepository deviceRepository,
//                         DeviceSettingRepository deviceSettingRepository,
//                         DeviceCommandRepository deviceCommandRepository,
                         HomeAccessService homeAccessService) {
        this.deviceRepository = deviceRepository;
//        this.deviceSettingRepository = deviceSettingRepository;
//        this.deviceCommandRepository = deviceCommandRepository;
        this.homeAccessService = homeAccessService;
    }

    public List<DeviceDTO> getAllDevices(String userId) {
        throw new RuntimeException();
//        return deviceRepository.findByUserId(userId).stream()
//                .map(this::convertToDeviceDTO)
//                .collect(Collectors.toList());
    }

    public DeviceDTO registerDevice(String userId, NewDeviceDTO newDeviceDTO) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        // Logic to register a new device
//        Device device = new Device();
//        device.setDeviceName(newDeviceDTO.getDeviceName());
//        device.setDeviceType(newDeviceDTO.getDeviceType());
//        device.setHomeId(newDeviceDTO.getHomeId());
//        device.setMetadata(newDeviceDTO.getMetadata());
//
//        Device savedDevice = deviceRepository.save(device);
//        return convertToDeviceDTO(savedDevice);
    }

    public DeviceDetailDTO getDeviceById(String userId, String deviceId) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        Device device = deviceRepository.findById(deviceId)
//                .orElseThrow(() -> new RuntimeException("Device not found."));
//
//        List<DeviceSettingDTO> settings = deviceSettingRepository.findByDeviceId(deviceId).stream()
//                .map(this::convertToDeviceSettingDTO)
//                .collect(Collectors.toList());
//
//        List<DeviceSensorDTO> sensors = deviceCommandRepository.findByDeviceId(deviceId).stream()
//                .map(this::convertToDeviceSensorDTO)
//                .collect(Collectors.toList());
//
//        return new DeviceDetailDTO(device.getDeviceId(), device.getDeviceName(), device.getHomeId(), settings, sensors);
    }

    public void deleteDevice(String userId, String deviceId) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        Device device = deviceRepository.findById(deviceId)
//                .orElseThrow(() -> new RuntimeException("Device not found."));
//
//        deviceRepository.delete(device);
    }

    public List<DeviceSettingDTO> getDeviceSettings(String userId, String deviceId) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        return deviceSettingRepository.findByDeviceId(deviceId).stream()
//                .map(this::convertToDeviceSettingDTO)
//                .collect(Collectors.toList());
    }

    public DeviceSettingDTO updateDeviceSetting(String userId, String deviceId, UpdateDeviceSettingDTO updateDeviceSettingDTO) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        DeviceSetting deviceSetting = deviceSettingRepository.findById(updateDeviceSettingDTO.getSettingId())
//                .orElseThrow(() -> new RuntimeException("Device setting not found."));
//
//        deviceSetting.setSettingName(updateDeviceSettingDTO.getSettingName());
//        deviceSetting.setSettingValue(updateDeviceSettingDTO.getSettingValue());
//
//        DeviceSetting updatedSetting = deviceSettingRepository.save(deviceSetting);
//        return convertToDeviceSettingDTO(updatedSetting);
    }

    public void sendDeviceCommand(String userId, String deviceId, DeviceCommandDTO deviceCommandDTO) {
        throw new RuntimeException();
//        if (!homeAccessService.hasAccess(userId)) {
//            throw new RuntimeException("User does not have access.");
//        }
//        // Logic to send command to the device
//        // Assuming DeviceCommandRepository is responsible for persisting commands
//        DeviceCommand deviceCommand = new DeviceCommand();
//        deviceCommand.setDeviceId(deviceId);
//        deviceCommand.setCommand(deviceCommandDTO.getCommand());
//
//        deviceCommandRepository.save(deviceCommand);
//        // Add logic to communicate with device via integration
    }

    // Convert methods for DTOs (these should be implemented according to your needs)
//    private DeviceDTO convertToDeviceDTO(Device device) {
//        // Implementation
//    }

//    private DeviceSettingDTO convertToDeviceSettingDTO(DeviceSetting deviceSetting) {
//        // Implementation
//    }
//
//    private DeviceSensorDTO convertToDeviceSensorDTO(DeviceSensor deviceSensor) {
//        // Implementation
//    }
}
