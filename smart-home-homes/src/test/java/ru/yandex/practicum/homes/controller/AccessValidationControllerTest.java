package ru.yandex.practicum.homes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.homes.dto.AccessCheckRequestDTO;
import ru.yandex.practicum.homes.model.Home;
import ru.yandex.practicum.homes.repository.HomeRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class AccessValidationControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HomeRepository homeRepository;

    @BeforeEach
    void setUp() {
        // Очищаем репозиторий перед каждым тестом
        homeRepository.deleteAll();

        // Создаем тестовые данные
        Home home1 = new Home();
        home1.setHomeId("home-1");
        home1.setHomeName("Home 1");
        home1.setOwnerId("user-1");
        home1.setSharedUserIds(List.of("user-3"));
        homeRepository.save(home1);

        Home home2 = new Home();
        home2.setHomeId("home-2");
        home2.setHomeName("Home 2");
        home2.setOwnerId("user-2");
        homeRepository.save(home2);
    }

    @Test
    void testValidateAccess_OwnerAccess() throws Exception {
        AccessCheckRequestDTO requestDTO = new AccessCheckRequestDTO();
        requestDTO.setHomeId("home-1");
        requestDTO.setUserId("user-1");

        mockMvc.perform(post("/service/validate-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAccess").value(true));
    }

    @Test
    void testValidateAccess_GuestAccess() throws Exception {
        AccessCheckRequestDTO requestDTO = new AccessCheckRequestDTO();
        requestDTO.setHomeId("home-1");
        requestDTO.setUserId("user-3");

        mockMvc.perform(post("/service/validate-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAccess").value(true));
    }

    @Test
    void testValidateAccess_NoAccess() throws Exception {
        AccessCheckRequestDTO requestDTO = new AccessCheckRequestDTO();
        requestDTO.setHomeId("home-2");
        requestDTO.setUserId("user-1");

        mockMvc.perform(post("/service/validate-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAccess").value(false));
    }

    @Test
    void testValidateAccess_HomeNotFound() throws Exception {
        AccessCheckRequestDTO requestDTO = new AccessCheckRequestDTO();
        requestDTO.setHomeId("home-5");
        requestDTO.setUserId("user-1");

        mockMvc.perform(post("/service/validate-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidateAccess_UserNotFound() throws Exception {
        AccessCheckRequestDTO requestDTO = new AccessCheckRequestDTO();
        requestDTO.setHomeId("home-1");
        requestDTO.setUserId("user-5");

        mockMvc.perform(post("/service/validate-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAccess").value(false));
    }
}
