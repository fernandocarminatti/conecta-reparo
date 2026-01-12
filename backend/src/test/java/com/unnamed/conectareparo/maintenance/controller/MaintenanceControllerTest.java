package com.unnamed.conectareparo.maintenance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.common.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.maintenance.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(MaintenanceController.class)
@DisplayName("Maintenance Controller Integration Tests")
class MaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaintenanceService maintenanceService;

    private UUID validPublicId;
    private UUID notFoundPublicId;
    private MaintenanceResponseDto maintenanceResponseDto;
    private ZonedDateTime validDate;

    @BeforeEach
    void setUp() {
        validPublicId = UUID.randomUUID();
        notFoundPublicId = UUID.randomUUID();
        validDate = ZonedDateTime.parse("3333-10-10T10:10:00Z");
        maintenanceResponseDto = new MaintenanceResponseDto(
                validPublicId, "Test Title", "Test Desc", MaintenanceCategory.ELECTRICAL,
                validDate, MaintenanceStatus.OPEN, ZonedDateTime.now(), ZonedDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /api/v1/maintenances")
    class CreateMaintenanceTests {
        @Test
        @DisplayName("Should return 201 Created when request is valid")
        void shouldReturn201_whenRequestIsValid() throws Exception {
            MaintenanceDto requestDto = new MaintenanceDto(
                    "New Title",
                    "New Desc",
                    MaintenanceCategory.ELECTRICAL,
                    validDate);
            when(maintenanceService.createMaintenance(any(MaintenanceDto.class))).thenReturn(maintenanceResponseDto);

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(validPublicId.toString()))
                    .andExpect(jsonPath("$.title").value("Test Title"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when title is blank")
        void shouldReturn400_whenTitleIsBlank() throws Exception {
            MaintenanceDto requestDto = new MaintenanceDto("", "Desc", MaintenanceCategory.ELECTRICAL, null);

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/maintenances")
    class GetAllMaintenancesTests {
        @Test
        @DisplayName("Should return 200 OK with a page of maintenances")
        void shouldReturn200_withPageOfMaintenances() throws Exception {
            Page<MaintenanceResponseDto> page = new PageImpl<>(List.of(maintenanceResponseDto), PageRequest.of(0, 10), 1);
            when(maintenanceService.getAllMaintenances(any(), any(), any(), any(PageRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/v1/maintenances")
                            .param("status", "open")
                            .param("search", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(validPublicId.toString()))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/maintenances/{id}")
    class GetMaintenanceByIdTests {
        @Test
        @DisplayName("Should return 200 OK with maintenance data when ID is found")
        void shouldReturn200_whenIdIsFound() throws Exception {
            when(maintenanceService.getMaintenanceByPublicId(validPublicId)).thenReturn(maintenanceResponseDto);

            mockMvc.perform(get("/api/v1/maintenances/{id}", validPublicId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validPublicId.toString()));
        }

        @Test
        @DisplayName("Should return 404 Not Found when ID does not exist")
        void shouldReturn404_whenIdIsNotFound() throws Exception {
            when(maintenanceService.getMaintenanceByPublicId(notFoundPublicId)).thenThrow(new ResourceNotFoundException("Not found"));

            mockMvc.perform(get("/api/v1/maintenances/{id}", notFoundPublicId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/maintenances/{id}")
    class UpdateMaintenanceTests {
        @Test
        @DisplayName("Should return 200 OK with updated data when request is valid")
        void shouldReturn200_whenRequestIsValid() throws Exception {
            MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto("Updated Title", null, null, null);
            MaintenanceResponseDto updatedResponse = new MaintenanceResponseDto(validPublicId, "Updated Title", "Test Desc", MaintenanceCategory.ELECTRICAL, null, MaintenanceStatus.OPEN, ZonedDateTime.now(), ZonedDateTime.now());

            when(maintenanceService.updateMaintenance(eq(validPublicId), any(MaintenanceUpdateDto.class))).thenReturn(updatedResponse);

            mockMvc.perform(patch("/api/v1/maintenances/{id}", validPublicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        @Test
        @DisplayName("Should return 404 Not Found when ID to update does not exist")
        void shouldReturn404_whenIdToUpdateIsNotFound() throws Exception {
            MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto("Updated Title", null, null, null);
            when(maintenanceService.updateMaintenance(eq(notFoundPublicId), any(MaintenanceUpdateDto.class))).thenThrow(new ResourceNotFoundException("Not found"));

            mockMvc.perform(patch("/api/v1/maintenances/{id}", notFoundPublicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Invalid Request Handling")
    class InvalidRequestHandlingTests {

        @Test
        @DisplayName("Should return 400 Bad Request for invalid UUID format")
        void shouldReturn400_whenUuidIsInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/maintenances/{id}", "not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request for malformed JSON body")
        void shouldReturn400_whenJsonIsMalformed() throws Exception {
            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json: }"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title is null")
        void shouldReturn400_whenTitleIsNull() throws Exception {
            MaintenanceDto requestDto = new MaintenanceDto(null, "Description", MaintenanceCategory.ELECTRICAL, validDate);

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when title contains only whitespace")
        void shouldReturn400_whenTitleIsOnlyWhitespace() throws Exception {
            MaintenanceDto requestDto = new MaintenanceDto("   ", "Description", MaintenanceCategory.ELECTRICAL, validDate);

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when category is invalid enum value")
        void shouldReturn400_whenCategoryIsInvalid() throws Exception {
            String invalidJson = "{\"title\": \"Test\", \"description\": \"Desc\", \"category\": \"INVALID_CATEGORY\", \"scheduledDate\": \"2025-12-31T10:00:00Z\"}";

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when scheduledDate format is invalid")
        void shouldReturn400_whenScheduledDateFormatIsInvalid() throws Exception {
            String invalidJson = "{\"title\": \"Test\", \"description\": \"Desc\", \"category\": \"ELECTRICAL\", \"scheduledDate\": \"invalid-date\"}";

            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for empty request body")
        void shouldReturn400_whenBodyIsEmpty() throws Exception {
            mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when patching with completely invalid JSON")
        void shouldReturn400_whenPatchJsonIsMalformed() throws Exception {
            mockMvc.perform(patch("/api/v1/maintenances/{id}", validPublicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{malformed: json: }"))
                    .andExpect(status().isBadRequest());
        }
    }
}