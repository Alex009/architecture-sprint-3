package ru.yandex.practicum.telemetry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.dto.TelemetryDataDTO;
import ru.yandex.practicum.telemetry.exception.UnauthorizedException;
import ru.yandex.practicum.telemetry.service.TelemetryService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/devices")
public class TelemetryController {

    private final TelemetryService telemetryService;

    @Autowired
    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/{deviceId}/telemetry/latest")
    public TelemetryDataDTO getLatestTelemetry(@PathVariable String deviceId) {
        String userId = getAuthenticatedUserId();

        // Получаем последние данные телеметрии с использованием сервиса
        return telemetryService.getLatestTelemetry(deviceId, userId);
    }

    @GetMapping("/{deviceId}/telemetry")
    public List<TelemetryDataDTO> getHistoricalTelemetry(
            @PathVariable String deviceId,
            @RequestParam("startDate") Instant startDate,
            @RequestParam("endDate") Instant endDate) {
        String userId = getAuthenticatedUserId();

        // Получаем исторические данные телеметрии с использованием сервиса
        return telemetryService.getHistoricalTelemetry(deviceId, startDate, endDate, userId);
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
