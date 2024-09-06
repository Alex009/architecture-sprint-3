package ru.yandex.practicum.telemetry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.telemetry.dto.DeviceAccessRequestDTO;
import ru.yandex.practicum.telemetry.dto.DeviceAccessResponseDTO;
import ru.yandex.practicum.telemetry.exception.DevicesServiceException;

@Service
public class DeviceAccessService {

    private final RestTemplate restTemplate;
    private final String devicesServiceUrl;

    @Autowired
    public DeviceAccessService(
            RestTemplate restTemplate,
            @Value("${devices-service.url}") String devicesServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.devicesServiceUrl = devicesServiceUrl;
    }

    public boolean validateAccess(String userId, String deviceId) {
        // Формируем тело запроса для Home сервиса
        DeviceAccessRequestDTO request = new DeviceAccessRequestDTO(
                userId,
                deviceId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeviceAccessRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            // Отправляем POST-запрос в Home сервис и получаем результат
            DeviceAccessResponseDTO response = restTemplate.postForObject(
                    devicesServiceUrl + "/service/validate-access",
                    entity,
                    DeviceAccessResponseDTO.class
            );

            if (response == null) {
                throw new DevicesServiceException("Received null response from Devices Service");
            }

            // Проверяем, имеет ли пользователь доступ
            return response.isHasAccess();
        } catch (RestClientException e) {
            // Если получаем ошибку от сервиса домов, выбрасываем исключение
            throw new DevicesServiceException("Error occurred while contacting Devices Service", e);
        }
    }
}
