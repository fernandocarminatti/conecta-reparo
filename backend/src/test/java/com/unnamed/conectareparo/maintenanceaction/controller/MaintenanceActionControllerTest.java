package com.unnamed.conectareparo.maintenanceaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.common.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionUpdateDto;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionStatus;
import com.unnamed.conectareparo.maintenanceaction.service.MaintenanceActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(MaintenanceActionController.class)
@DisplayName("Maintenance Action Controller Integration Tests")
class MaintenanceActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaintenanceActionService maintenanceActionService;

    private UUID validMaintenanceId;
    private UUID validActionId;
    private UUID notFoundActionId;
    private MaintenanceActionResponseDto actionResponseDto;
    private ZonedDateTime randomPointInTime;

    @BeforeEach
    void setUp() {
        validMaintenanceId = UUID.randomUUID();
        validActionId = UUID.randomUUID();
        notFoundActionId = UUID.randomUUID();
        randomPointInTime = ZonedDateTime.parse("2024-10-10T10:10:00Z");
        actionResponseDto = new MaintenanceActionResponseDto(
                validActionId,
                validMaintenanceId,
                "Test Maintenance",
                "John Doe",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                "Action performed",
                Collections.emptyList(),
                ActionStatus.SUCCESS,
                randomPointInTime,
                ZonedDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /api/v1/maintenances/{maintId}/actions")
    class CreateActionTests {
        @Test
        @DisplayName("Should return 201 Created when request is valid")
        void shouldReturn201_whenRequestIsValid() throws Exception {
            MaintenanceActionDto requestDto = new MaintenanceActionDto(
                    "John Doe",
                    randomPointInTime,
                    randomPointInTime,
                    "Action performed",
                    Collections.emptyList(),
                    ActionStatus.SUCCESS);
            when(maintenanceActionService.createMaintenanceAction(eq(validMaintenanceId), any(MaintenanceActionDto.class))).thenReturn(actionResponseDto);

            mockMvc.perform(post("/api/v1/maintenances/{maintenancePublicId}/actions", validMaintenanceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(validActionId.toString()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/maintenances/{maintId}/actions")
    class ListActionsTests {
        @Test
        @DisplayName("Should return 200 OK with a list of actions")
        void shouldReturn200_withListOfActions() throws Exception {
            when(maintenanceActionService.getMaintenanceActions(validMaintenanceId)).thenReturn(List.of(actionResponseDto));

            mockMvc.perform(get("/api/v1/maintenances/{maintenancePublicId}/actions", validMaintenanceId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(validActionId.toString()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/maintenances/{maintId}/actions/{actionId}")
    class GetSingleActionTests {
        @Test
        @DisplayName("Should return 200 OK when both IDs are valid and associated")
        void shouldReturn200_whenIdsAreValid() throws Exception {
            when(maintenanceActionService.getSingleMaintenanceAction(validMaintenanceId, validActionId)).thenReturn(actionResponseDto);

            mockMvc.perform(get("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}", validMaintenanceId, validActionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validActionId.toString()));
        }

        @Test
        @DisplayName("Should return 404 Not Found when action is not found for the maintenance")
        void shouldReturn404_whenActionIsNotFound() throws Exception {
            when(maintenanceActionService.getSingleMaintenanceAction(validMaintenanceId, notFoundActionId)).thenThrow(new ResourceNotFoundException("Not found"));

            mockMvc.perform(get("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}", validMaintenanceId, notFoundActionId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/maintenances/{maintId}/actions/{actionId}")
    class UpdateActionTests {
        @Test
        @DisplayName("Should return 200 OK with updated data when request is valid")
        void shouldReturn200_whenRequestIsValid() throws Exception {
            MaintenanceActionUpdateDto updateDto = new MaintenanceActionUpdateDto(
                    "Jane Doe",
                    randomPointInTime,
                    randomPointInTime,
                    "Updated action",
                    Collections.emptyList(),
                    ActionStatus.PARTIAL_SUCCESS);
            MaintenanceActionResponseDto updatedResponse = new MaintenanceActionResponseDto(
                    validActionId,
                    validMaintenanceId,
                    "Test Maintenance",
                    "Jane Doe",
                    randomPointInTime,
                    randomPointInTime,
                    "Updated action",
                    Collections.emptyList(),
                    ActionStatus.SUCCESS,
                    randomPointInTime,
                    ZonedDateTime.now());

            when(maintenanceActionService.updateMaintenanceAction(eq(validMaintenanceId), eq(validActionId), any(MaintenanceActionUpdateDto.class))).thenReturn(updatedResponse);

            mockMvc.perform(put("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}", validMaintenanceId, validActionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.executedBy").value("Jane Doe"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/actions (All Actions)")
    class GetAllActionsTests {
        @Test
        @DisplayName("Should return 200 OK with all actions")
        void shouldReturn200_withAllActions() throws Exception {
            when(maintenanceActionService.getAllActions()).thenReturn(List.of(actionResponseDto));

            mockMvc.perform(get("/api/v1/actions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(validActionId.toString()))
                    .andExpect(jsonPath("$[0].executedBy").value("John Doe"));
        }

        @Test
        @DisplayName("Should return 200 OK with empty list when no actions exist")
        void shouldReturn200_withEmptyList() throws Exception {
            when(maintenanceActionService.getAllActions()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/actions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Invalid Request Handling")
    class InvalidRequestHandlingTests {

        @Test
        @DisplayName("Should return 400 Bad Request for invalid maintenance ID format")
        void shouldReturn400_whenMaintenanceIdIsInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/maintenances/{maintenancePublicId}/actions", "not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request for invalid action ID format")
        void shouldReturn400_whenActionIdIsInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}", validMaintenanceId, "not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request for malformed JSON body")
        void shouldReturn400_whenJsonIsMalformed() throws Exception {
            mockMvc.perform(post("/api/v1/maintenances/{maintenancePublicId}/actions", validMaintenanceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json: }"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when executedBy is blank")
        void shouldReturn400_whenExecutedByIsBlank() throws Exception {
            MaintenanceActionDto requestDto = new MaintenanceActionDto(
                    "",
                    randomPointInTime,
                    randomPointInTime,
                    "Action performed",
                    Collections.emptyList(),
                    ActionStatus.SUCCESS);

            mockMvc.perform(post("/api/v1/maintenances/{maintenancePublicId}/actions", validMaintenanceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for empty request body")
        void shouldReturn400_whenBodyIsEmpty() throws Exception {
            mockMvc.perform(post("/api/v1/maintenances/{maintenancePublicId}/actions", validMaintenanceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when patching with malformed JSON")
        void shouldReturn400_whenPatchJsonIsMalformed() throws Exception {
            mockMvc.perform(put("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}", validMaintenanceId, validActionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{malformed: json}"))
                    .andExpect(status().isBadRequest());
        }
    }
}