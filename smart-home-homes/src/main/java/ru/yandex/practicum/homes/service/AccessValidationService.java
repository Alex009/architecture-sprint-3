package ru.yandex.practicum.homes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.homes.exception.HomeNotFoundException;
import ru.yandex.practicum.homes.model.Home;
import ru.yandex.practicum.homes.repository.HomeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessValidationService {

    private final HomeRepository homeRepository;

    @Autowired
    public AccessValidationService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public boolean validateAccess(String userId, String homeId) {
        // Найти дом по идентификатору
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new HomeNotFoundException("Home not found with ID: " + homeId));

        // Проверка, имеет ли пользователь доступ к этому дому
        return home.hasAccess(userId);
    }

    public List<String> getAccessibleHomes(String userId) {
        // Получаем список домов, к которым у пользователя есть доступ
        List<Home> accessibleHomes = homeRepository.findByOwnerIdOrSharedUserIdsContains(userId, userId);

        // Преобразуем список объектов Home в список идентификаторов домов
        return accessibleHomes.stream()
                .map(Home::getHomeId)
                .collect(Collectors.toList());
    }
}
