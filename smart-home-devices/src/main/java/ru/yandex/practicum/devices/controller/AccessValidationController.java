package ru.yandex.practicum.devices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.devices.dto.AccessCheckRequestDTO;
import ru.yandex.practicum.devices.dto.AccessCheckResponseDTO;
import ru.yandex.practicum.devices.service.AccessValidationService;

@RestController
@RequestMapping("/service")
public class AccessValidationController {

    private final AccessValidationService accessValidationService;

    @Autowired
    public AccessValidationController(AccessValidationService accessValidationService) {
        this.accessValidationService = accessValidationService;
    }

    @PostMapping("/validate-access")
    public ResponseEntity<AccessCheckResponseDTO> validateAccess(@RequestBody AccessCheckRequestDTO request) {
        boolean hasAccess = accessValidationService.validateAccess(request.getUserId(), request.getDeviceId());

        AccessCheckResponseDTO response = new AccessCheckResponseDTO();
        response.setHasAccess(hasAccess);

        return ResponseEntity.ok(response);
    }
}
