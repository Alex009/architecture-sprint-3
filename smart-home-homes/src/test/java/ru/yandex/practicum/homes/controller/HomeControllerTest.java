package ru.yandex.practicum.homes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.homes.dto.HomeDTO;
import ru.yandex.practicum.homes.model.Home;
import ru.yandex.practicum.homes.repository.HomeRepository;

import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class HomeControllerTest {

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
        registry.add("spring.security.jwt.secret", () -> "1be58f3f3a4c8277dd28898f7eb2f9a66f20fcdc74d79a42a8d2314c39745931b4c5a8be869085285111ce64e02a20ec534394eb9033fb5e85f400a3ba1fe3dd057644708559a835a97de35078a9b741abb4c4cb89447c481a82794d1b364c374145f7fdc79cba2e03617e2b70be743f9d9f5390aab7dd8ed3eeaf4d075ff19c");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HomeRepository homeRepository;

    @Test
    @WithMockUser(username = "owner-1", roles = "USER")
    void testCreateHome() throws Exception {
        HomeDTO homeDTO = new HomeDTO();
        homeDTO.setHomeName("Test Home");
        homeDTO.setOwnerId("owner-1");

        mockMvc.perform(post("/homes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(homeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeName").value("Test Home"))
                .andExpect(jsonPath("$.ownerId").value("owner-1"));
    }

    @Test
    @WithMockUser(username = "owner-1", roles = "USER")
    void testGetHome() throws Exception {
        Home home = new Home();
        home.setHomeId(UUID.randomUUID().toString());
        home.setHomeName("Test Home");
        home.setOwnerId("owner-1");
        home = homeRepository.save(home);

        mockMvc.perform(get("/homes/{homeId}", home.getHomeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId").value(home.getHomeId()))
                .andExpect(jsonPath("$.homeName").value("Test Home"))
                .andExpect(jsonPath("$.ownerId").value("owner-1"));
    }

    @Test
    @WithMockUser(username = "owner-1", roles = "USER")
    void testUpdateHome() throws Exception {
        Home home = new Home();
        home.setHomeId(UUID.randomUUID().toString());
        home.setHomeName("Old Home Name");
        home.setOwnerId("owner-1");
        home = homeRepository.save(home);

        HomeDTO updateDto = new HomeDTO();
        updateDto.setHomeName("Updated Home Name");
        updateDto.setOwnerId("owner-1");

        mockMvc.perform(put("/homes/{homeId}", home.getHomeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeId").value(home.getHomeId()))
                .andExpect(jsonPath("$.homeName").value("Updated Home Name"))
                .andExpect(jsonPath("$.ownerId").value("owner-1"));
    }

    @Test
    @WithMockUser(username = "owner-1", roles = "USER")
    void testDeleteHome() throws Exception {
        Home home = new Home();
        home.setHomeId(UUID.randomUUID().toString());
        home.setHomeName("Test Home");
        home.setOwnerId("owner-1");
        home = homeRepository.save(home);

        mockMvc.perform(delete("/homes/{homeId}", home.getHomeId()))
                .andExpect(status().isNoContent());

        boolean exists = homeRepository.existsById(home.getHomeId());
        assertFalse(exists);
    }

    @Test
    @WithMockUser(username = "owner-1", roles = "USER")
    void testGetNonExistentHome() throws Exception {
        mockMvc.perform(get("/homes/{homeId}", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user-2", roles = "USER")
    void testAccessDeniedForHome() throws Exception {
        Home home = new Home();
        home.setHomeId(UUID.randomUUID().toString());
        home.setHomeName("Test Home");
        home.setOwnerId("owner-1");
        home = homeRepository.save(home);

        // Попытка получить доступ к дому с другого пользователя
        mockMvc.perform(get("/homes/{homeId}", home.getHomeId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testAccessDeniedForAnonymous() throws Exception {
        // Попытка получить доступ к дому с другого пользователя
        mockMvc.perform(get("/homes/{homeId}", "home-1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRealJWT() throws Exception {
        mockMvc.perform(
                get("/homes")
                        .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3MjUyMDU2NTksImV4cCI6MTc1Njc0MTY1OSwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidXNlci0xIn0.KSz5Y27TIApz3Og6J_xc0NposG-NjUsQlehNuJxlmRE")
        ).andExpect(status().isOk());
    }
}
