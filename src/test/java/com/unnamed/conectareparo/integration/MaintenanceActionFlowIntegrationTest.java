package com.unnamed.conectareparo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.dto.*;
import com.unnamed.conectareparo.entity.ActionOutcomeStatus;
import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
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
@DisplayName("Maintenance Action Integration Test")
class MaintenanceActionFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID maintenancePublicId;

    @BeforeEach
    void setUp() throws Exception {
        this.maintenancePublicId = createMaintenanceAndGetId("Parent Maintenance for Action Test");
    }

    @Test
    @DisplayName("Happy Path: Should create, get, and update a maintenance action successfully")
    void shouldExecuteActionLifecycleSuccessfully() throws Exception {
        NewActionMaterialDto materialDto = new NewActionMaterialDto(
                "Wrench",
                BigDecimal.ONE,
                "unit");
        NewMaintenanceActionDto createActionDto = new NewMaintenanceActionDto(
                "Mechanic Bob",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                "Tightened all bolts.",
                List.of(materialDto),
                ActionOutcomeStatus.PARTIAL_SUCCESS
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/maintenances/{maintId}/actions", maintenancePublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createActionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.executedBy").value("Mechanic Bob"))
                .andExpect(jsonPath("$.materialsUsed", hasSize(1)))
                .andExpect(jsonPath("$.materialsUsed[0].itemName").value("Wrench"))
                .andReturn();

        UUID createdActionId = objectMapper.readValue(createResult.getResponse().getContentAsString(), MaintenanceActionResponseDto.class).id();
        assertNotNull(createdActionId);

        mockMvc.perform(get("/api/v1/maintenances/{maintId}/actions/{actionId}", maintenancePublicId, createdActionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdActionId.toString()));

        NewActionMaterialDto updatedMaterialDto = new NewActionMaterialDto(
                "Hammer",
                BigDecimal.TEN,
                "unit");
        UpdateMaintenanceActionDto updateDto = new UpdateMaintenanceActionDto(
                "Mechanic Bob",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(2),
                "Tightened bolts and hammered panel.",
                List.of(updatedMaterialDto),
                ActionOutcomeStatus.SUCCESS
        );

        mockMvc.perform(put("/api/v1/maintenances/{maintId}/actions/{actionId}", maintenancePublicId, createdActionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionDescription").value("Tightened bolts and hammered panel."))
                .andExpect(jsonPath("$.materialsUsed", hasSize(1)))
                .andExpect(jsonPath("$.materialsUsed[0].itemName").value("Hammer"))
                .andExpect(jsonPath("$.outcomeStatus").value(ActionOutcomeStatus.SUCCESS.toString()));
    }

    @Test
    @DisplayName("Unhappy Path: Should return 409 when creating an action for an COMPLETED maintenance")
    void createAction_forOpenMaintenance_shouldReturn409() throws Exception {
        completeMaintenance(maintenancePublicId);
        NewMaintenanceActionDto createActionDto = new NewMaintenanceActionDto(
                "Mechanic Bob",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                "Work in progress",
                Collections.emptyList(),
                ActionOutcomeStatus.PARTIAL_SUCCESS
        );

        mockMvc.perform(post("/api/v1/maintenances/{maintId}/actions", maintenancePublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createActionDto)))
                .andExpect(status().isConflict());
    }

    private UUID createMaintenanceAndGetId(String title) throws Exception {
        NewMaintenanceRequestDto requestDto = new NewMaintenanceRequestDto(
                title,
                "Test Description",
                MaintenanceCategory.OTHERS,
                ZonedDateTime.now().plusDays(10)
        );
        MvcResult result = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
    }

    private void completeMaintenance(UUID maintenanceId) throws Exception {
        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto(null, null, null, MaintenanceStatus.COMPLETED);
        mockMvc.perform(patch("/api/v1/maintenances/{publicId}", maintenanceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}