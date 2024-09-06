package ru.yandex.practicum.devices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.devices.dto.*;
import ru.yandex.practicum.devices.exception.UnauthorizedException;
import ru.yandex.practicum.devices.service.DeviceService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        String userId = getAuthenticatedUserId();
        List<DeviceDTO> devices = deviceService.getAllDevices(userId);
        return ResponseEntity.ok(devices);
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> registerDevice(@RequestBody NewDeviceDTO newDeviceDTO) {
        String userId = getAuthenticatedUserId();
        DeviceDTO device = deviceService.registerDevice(userId, newDeviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceDetailDTO> getDeviceById(@PathVariable String deviceId) {
        String userId = getAuthenticatedUserId();
        DeviceDetailDTO deviceDetail = deviceService.getDeviceById(userId, deviceId);
        return ResponseEntity.ok(deviceDetail);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String deviceId) {
        String userId = getAuthenticatedUserId();
        deviceService.deleteDevice(userId, deviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deviceId}/settings")
    public ResponseEntity<List<DeviceSettingDTO>> getDeviceSettings(@PathVariable String deviceId) {
        String userId = getAuthenticatedUserId();
        List<DeviceSettingDTO> settings = deviceService.getDeviceSettings(userId, deviceId);
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/{deviceId}/settings")
    public ResponseEntity<DeviceSettingDTO> updateDeviceSetting(@PathVariable String deviceId, @RequestBody UpdateDeviceSettingDTO updateDeviceSettingDTO) {
        String userId = getAuthenticatedUserId();
        deviceService.updateDeviceSetting(userId, deviceId, updateDeviceSettingDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{deviceId}/command")
    public ResponseEntity<Void> sendDeviceCommand(@PathVariable String deviceId, @RequestBody DeviceCommandDTO deviceCommandDTO) {
        String userId = getAuthenticatedUserId();
        deviceService.sendDeviceCommand(userId, deviceId, deviceCommandDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        // используем User чтобы использовать WithMockUser в тестах, что проще
        var user = (User) authentication.getPrincipal();
        return user.getUsername();
    }
}
