package com.unnamed.conectareparo.pledge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.pledge.dto.PledgeDto;
import com.unnamed.conectareparo.pledge.dto.PledgeResponseDto;
import com.unnamed.conectareparo.pledge.dto.PledgeUpdateDto;
import com.unnamed.conectareparo.pledge.entity.PledgeCategory;
import com.unnamed.conectareparo.pledge.entity.PledgeStatus;
import com.unnamed.conectareparo.pledge.service.PledgeService;
import com.unnamed.conectareparo.common.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(PledgeController.class)
@DisplayName("Pledge Controller Integration Tests")
class PledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PledgeService pledgeService;

    private UUID validMaintenanceId;
    private UUID validPledgeId;
    private UUID notFoundPledgeId;
    private PledgeResponseDto pledgeResponseDto;
    private ZonedDateTime now;

    @BeforeEach
    void setUp() {
        validMaintenanceId = UUID.randomUUID();
        validPledgeId = UUID.randomUUID();
        notFoundPledgeId = UUID.randomUUID();
        now = ZonedDateTime.parse("2024-10-10T10:10:00Z");
        pledgeResponseDto = new PledgeResponseDto(
                validPledgeId,
                "John Doe",
                "555-1234",
                "I can help",
                PledgeCategory.LABOR,
                PledgeStatus.PENDING,
                now,
                now
        );
    }

    @Nested
    @DisplayName("POST /api/v1/pledges")
    class CreatePledgeTests {
        @Test
        @DisplayName("Should return 201 Created when request is valid")
        void shouldReturn201_whenRequestIsValid() throws Exception {
            PledgeDto requestDto = new PledgeDto(
                    validMaintenanceId,
                    "John Doe",
                    "555-1234",
                    "I can help",
                    PledgeCategory.LABOR,
                    PledgeStatus.OFFERED);
            when(pledgeService.createPledge(any(PledgeDto.class))).thenReturn(pledgeResponseDto);

            mockMvc.perform(post("/api/v1/pledges")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(validPledgeId.toString()))
                    .andExpect(jsonPath("$.volunteerName").value("John Doe"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when maintenanceId is null")
        void shouldReturn400_whenMaintenanceIdIsNull() throws Exception {
            PledgeDto requestDto = new PledgeDto(
                    null,
                    "John Doe",
                    "555-1234",
                    "I can help",
                    PledgeCategory.MATERIAL,
                    PledgeStatus.OFFERED);

            mockMvc.perform(post("/api/v1/pledges")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pledges")
    class GetPledgesForMaintenanceTests {
        @Test
        @DisplayName("Should return 200 OK with a page of pledges")
        void shouldReturn200_withPageOfPledges() throws Exception {
            Page<PledgeResponseDto> page = new PageImpl<>(List.of(pledgeResponseDto), PageRequest.of(0, 5), 1);
            when(pledgeService.getPledgesByMaintenanceId(any(), eq(validMaintenanceId))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/pledges")
                            .param("maintenanceId", validMaintenanceId.toString())
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(validPledgeId.toString()))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/pledges/{pledgeId}")
    class UpdatePledgeTests {
        @Test
        @DisplayName("Should return 200 OK with updated data when request is valid")
        void shouldReturn200_whenRequestIsValid() throws Exception {
            PledgeUpdateDto updateDto = new PledgeUpdateDto(
                    null,
                    null,
                    null,
                    null,
                    PledgeStatus.COMPLETED);
            PledgeResponseDto updatedResponse = new PledgeResponseDto(
                    validPledgeId,
                    "John Doe",
                    "555-1234",
                    "I can help",
                    PledgeCategory.MATERIAL,
                    PledgeStatus.COMPLETED,
                    now,
                    now);

            when(pledgeService.updatePledge(eq(validPledgeId), any(PledgeUpdateDto.class))).thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(patch("/api/v1/pledges/{pledgeId}", validPledgeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(PledgeStatus.COMPLETED.toString()));
        }

        @Test
        @DisplayName("Should return 404 Not Found when pledge ID does not exist")
        void shouldReturn404_whenPledgeIdIsNotFound() throws Exception {
            PledgeUpdateDto updateDto = new PledgeUpdateDto(
                    null,
                    null,
                    null,
                    null,
                    PledgeStatus.COMPLETED);
            when(pledgeService.updatePledge(eq(notFoundPledgeId), any(PledgeUpdateDto.class))).thenThrow(new ResourceNotFoundException("Not found"));

            mockMvc.perform(patch("/api/v1/pledges/{pledgeId}", notFoundPledgeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());
        }
    }
}