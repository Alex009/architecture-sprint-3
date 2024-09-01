package ru.yandex.practicum.homes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.homes.dto.HomeDTO;
import ru.yandex.practicum.homes.exception.AccessDeniedException;
import ru.yandex.practicum.homes.exception.HomeNotFoundException;
import ru.yandex.practicum.homes.model.Home;
import ru.yandex.practicum.homes.repository.HomeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HomeService {

    private final HomeRepository homeRepository;

    @Autowired
    public HomeService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public List<HomeDTO> getHomesByUserId(String userId) {
        List<Home> homes = homeRepository.findByOwnerIdOrSharedUserIdsContains(userId, userId);
        return homes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HomeDTO getHomeByIdAndUserId(String homeId, String userId) {
        Home home = getHome(homeId, userId);
        return toDTO(home);
    }

    public HomeDTO createHome(HomeDTO homeDTO) {
        Home home = new Home();
        home.setHomeId(UUID.randomUUID().toString());
        home.setHomeName(homeDTO.getHomeName());
        home.setOwnerId(homeDTO.getOwnerId());
        home = homeRepository.save(home);
        return toDTO(home);
    }

    public HomeDTO updateHome(String homeId, HomeDTO homeDTO, String userId) {
        Home home = getHome(homeId, userId);

        home.setHomeName(homeDTO.getHomeName());
        home = homeRepository.save(home);
        return toDTO(home);
    }

    public void deleteHome(String homeId, String userId) {
        Home home = getHome(homeId, userId);

        homeRepository.delete(home);
    }

    private Home getHome(String homeId, String userId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new HomeNotFoundException("Home not found with ID: " + homeId));

        if (!home.hasAccess(userId)) {
            throw new AccessDeniedException("Access denied to home with ID: " + homeId);
        }
        return home;
    }

    private HomeDTO toDTO(Home home) {
        HomeDTO homeDTO = new HomeDTO();
        homeDTO.setHomeId(home.getHomeId());
        homeDTO.setHomeName(home.getHomeName());
        homeDTO.setOwnerId(home.getOwnerId());
        return homeDTO;
    }
}
