package com.unnamed.conectareparo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Maintenance Flow Integration Tests")
public class MaintenanceFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static UUID createdMaintenanceId;

    @Test
    @DisplayName("Should create, retrieve, update, and appear in list successfully")
    void shouldExecuteFullLifecycle() throws Exception {
        NewMaintenanceRequestDto createDto = new NewMaintenanceRequestDto(
                "Lifecycle Test: Fix Window",
                "Window is cracked.",
                MaintenanceCategory.PREDIAL,
                ZonedDateTime.now().plusDays(10)
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        UUID createdId = objectMapper.readValue(createResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances/{publicId}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lifecycle Test: Fix Window"));

        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto(
                "Lifecycle Test: Window Replaced",
                null,
                null,
                MaintenanceStatus.COMPLETED
        );

        mockMvc.perform(patch("/api/v1/maintenances/{publicId}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lifecycle Test: Window Replaced"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/api/v1/maintenances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == '" + createdId + "')].status").value(MaintenanceStatus.COMPLETED.toString()));
    }


    @Test
    @DisplayName("Unhappy Path: Should return 400 for invalid creation request")
    void createMaintenance_withInvalidData_shouldReturn400() throws Exception {
        NewMaintenanceRequestDto requestDto = new NewMaintenanceRequestDto(
                "",
                "Desc",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Unhappy Path: Should return 404 for non-existent ID on GET")
    void getMaintenance_withNonExistentId_shouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/maintenances/{publicId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unhappy Path: Should return 404 for non-existent ID on PATCH")
    void updateMaintenance_withNonExistentId_shouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto("Title", null, null, null);

        mockMvc.perform(patch("/api/v1/maintenances/{publicId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }
}