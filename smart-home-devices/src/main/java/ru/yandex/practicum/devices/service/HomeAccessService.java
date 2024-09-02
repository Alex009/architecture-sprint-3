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
import ru.yandex.practicum.devices.exception.HomeServiceException;

import java.util.Arrays;
import java.util.List;

@Service
public class HomeAccessService {

    private final RestTemplate restTemplate;
    private final String homeServiceUrl;

    @Autowired
    public HomeAccessService(
            RestTemplate restTemplate,
            @Value("${home-service.url}") String homeServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.homeServiceUrl = homeServiceUrl;
    }

    public boolean validateAccess(String userId, String homeId) {
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

    public List<String> getAccessibleHomes(String userId) {
        String url = homeServiceUrl + "/service/homes?userId=" + userId;

        try {
            // Отправляем GET-запрос в Home сервис и получаем список идентификаторов домов
            String[] homeIds = restTemplate.getForObject(url, String[].class);

            if (homeIds == null) {
                throw new HomeServiceException("Received null response from Home Service");
            }

            // Преобразуем массив в список и возвращаем
            return Arrays.asList(homeIds);
        } catch (RestClientException e) {
            // Если получаем ошибку от сервиса домов, выбрасываем исключение
            throw new HomeServiceException("Error occurred while contacting Home Service", e);
        }
    }
}
