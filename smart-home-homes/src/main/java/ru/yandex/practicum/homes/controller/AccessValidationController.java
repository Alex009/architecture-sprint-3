package ru.yandex.practicum.homes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.homes.dto.AccessCheckRequestDTO;
import ru.yandex.practicum.homes.dto.AccessCheckResponseDTO;
import ru.yandex.practicum.homes.service.AccessValidationService;

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
        boolean hasAccess = accessValidationService.validateAccess(request.getUserId(), request.getHomeId());

        AccessCheckResponseDTO response = new AccessCheckResponseDTO();
        response.setHasAccess(hasAccess);

        return ResponseEntity.ok(response);
    }
}
