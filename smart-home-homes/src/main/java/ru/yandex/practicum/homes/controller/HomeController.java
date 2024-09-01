package ru.yandex.practicum.homes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.homes.dto.HomeDTO;
import ru.yandex.practicum.homes.exception.UnauthorizedException;
import ru.yandex.practicum.homes.service.HomeService;

import java.util.List;

@RestController
@RequestMapping("/homes")
public class HomeController {

    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<List<HomeDTO>> getAllHomes() {
        String userId = getAuthenticatedUserId();

        // Получение списка домов, к которым у пользователя есть доступ
        List<HomeDTO> homes = homeService.getHomesByUserId(userId);
        return ResponseEntity.ok(homes);
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<HomeDTO> getHomeById(@PathVariable String homeId) {
        String userId = getAuthenticatedUserId();

        // Получение дома по ID, если у пользователя есть доступ
        HomeDTO home = homeService.getHomeByIdAndUserId(homeId, userId);
        return ResponseEntity.ok(home);
    }

    @PostMapping
    public ResponseEntity<HomeDTO> createHome(@RequestBody HomeDTO homeDTO) {
        String userId = getAuthenticatedUserId();

        // Установка владельца как текущего пользователя
        homeDTO.setOwnerId(userId);
        HomeDTO createdHome = homeService.createHome(homeDTO);
        return ResponseEntity.ok(createdHome);
    }

    @PutMapping("/{homeId}")
    public ResponseEntity<HomeDTO> updateHome(@PathVariable String homeId, @RequestBody HomeDTO homeDTO) {
        String userId = getAuthenticatedUserId();

        // Обновление информации о доме, если у пользователя есть доступ
        HomeDTO updatedHome = homeService.updateHome(homeId, homeDTO, userId);
        return ResponseEntity.ok(updatedHome);
    }

    @DeleteMapping("/{homeId}")
    public ResponseEntity<Void> deleteHome(@PathVariable String homeId) {
        String userId = getAuthenticatedUserId();

        // Удаление дома, если у пользователя есть доступ
        homeService.deleteHome(homeId, userId);
        return ResponseEntity.noContent().build();
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
