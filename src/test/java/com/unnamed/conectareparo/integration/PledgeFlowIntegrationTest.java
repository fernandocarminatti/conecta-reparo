package com.unnamed.conectareparo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.dto.*;
import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.PledgeCategory;
import com.unnamed.conectareparo.entity.PledgeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Pledge Flow Integration Tests")
class PledgeFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID parentMaintenanceId;

    @BeforeEach
    void setUp() throws Exception {
        this.parentMaintenanceId = createMaintenanceAndGetId();
    }

    @Test
    @DisplayName("Happy Path: Should create, list, and update a pledge successfully")
    void shouldExecutePledgeLifecycleSuccessfully() throws Exception {
        PledgeDto createPledgeDto = new PledgeDto(
                parentMaintenanceId,
                "Volunteer Alice",
                "alice@example.com",
                "Offering to paint",
                PledgeCategory.LABOR,
                PledgeStatus.OFFERED
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/pledges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPledgeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.volunteerName").value("Volunteer Alice"))
                .andExpect(jsonPath("$.status").value(PledgeStatus.OFFERED.toString()))
                .andReturn();

        PledgeResponseDto createdPledge = objectMapper.readValue(createResult.getResponse().getContentAsString(), PledgeResponseDto.class);
        UUID createdPledgeId = createdPledge.id();
        assertNotNull(createdPledgeId);

        mockMvc.perform(get("/api/v1/pledges")
                        .param("maintenanceId", parentMaintenanceId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(createdPledgeId.toString()));

        PledgeUpdateDto updateDto = new PledgeUpdateDto(
                null,
                null,
                null,
                null,
                PledgeStatus.COMPLETED);

        mockMvc.perform(patch("/api/v1/pledges/{pledgeId}", createdPledgeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPledgeId.toString()))
                .andExpect(jsonPath("$.status").value(PledgeStatus.COMPLETED.toString()));
    }

    @Test
    @DisplayName("Unhappy Path: Should return 404 when creating a pledge for a non-existent maintenance")
    void createPledge_withNonExistentMaintenance_shouldReturn404() throws Exception {
        UUID nonExistentMaintenanceId = UUID.randomUUID();
        PledgeDto createPledgeDto = new PledgeDto(
                nonExistentMaintenanceId,
                "Volunteer Bob",
                "bob@example.com",
                "Offering help",
                PledgeCategory.LABOR,
                PledgeStatus.OFFERED
        );

        mockMvc.perform(post("/api/v1/pledges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPledgeDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unhappy Path: Should return 400 for invalid data when creating a pledge")
    void createPledge_withInvalidData_shouldReturn400() throws Exception {
        PledgeDto createPledgeDto = new PledgeDto(
                parentMaintenanceId,
                "",
                "",
                "",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/pledges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPledgeDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Helper method to create a Maintenance record via the API for test setup.
     */
    private UUID createMaintenanceAndGetId() throws Exception {
        MaintenanceDto requestDto = new MaintenanceDto(
                "Parent Maintenance for Pledge Test",
                "Test Description",
                MaintenanceCategory.OTHERS,
                ZonedDateTime.now().plusDays(10)
        );

        MvcResult result = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        MaintenanceResponseDto responseDto = objectMapper.readValue(jsonResponse, MaintenanceResponseDto.class);
        return responseDto.id();
    }
}