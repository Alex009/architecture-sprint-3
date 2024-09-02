package ru.yandex.practicum.devices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.devices.dto.HomeAccessRequestDTO;
import ru.yandex.practicum.devices.dto.HomeAccessResponseDTO;
import ru.yandex.practicum.devices.exception.DeviceNotFoundException;
import ru.yandex.practicum.devices.exception.HomeServiceException;
import ru.yandex.practicum.devices.model.Device;
import ru.yandex.practicum.devices.repository.DeviceRepository;

@Service
public class AccessValidationService {

    private final DeviceRepository deviceRepository;
    private final RestTemplate restTemplate;
    private final String homeServiceUrl;

    @Autowired
    public AccessValidationService(
            DeviceRepository deviceRepository,
            RestTemplate restTemplate,
            @Value("${home-service.url}") String homeServiceUrl
    ) {
        this.deviceRepository = deviceRepository;
        this.restTemplate = restTemplate;
        this.homeServiceUrl = homeServiceUrl;
    }

    public boolean validateAccess(String userId, String deviceId) {
        // Получаем устройство по deviceId
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        String homeId = device.getHomeId();

        // Формируем тело запроса для Home сервиса
        HomeAccessRequestDTO request = new HomeAccessRequestDTO();
        request.setUserId(userId);
        request.setHomeId(homeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HomeAccessRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            // Отправляем POST-запрос в Home сервис и получаем результат
            HomeAccessResponseDTO response = restTemplate.postForObject(
                    homeServiceUrl + "/service/validate-access",
                    entity,
                    HomeAccessResponseDTO.class
            );

            if (response == null) {
                throw new HomeServiceException("Received null response from Home Service");
            }

            // Проверяем, имеет ли пользователь доступ
            return response.isHasAccess();
        } catch (RestClientException e) {
            // Если получаем ошибку от сервиса домов, выбрасываем исключение
            throw new HomeServiceException("Error occurred while contacting Home Service", e);
        }
    }
}
