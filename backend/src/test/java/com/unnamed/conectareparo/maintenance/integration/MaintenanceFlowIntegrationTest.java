package com.unnamed.conectareparo.maintenance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.maintenance.repository.MaintenanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@DisplayName("Maintenance Flow Integration Tests")
public class MaintenanceFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    private static UUID createdMaintenanceId;

    @Test
    @DisplayName("Should create, retrieve, update, and appear in list successfully")
    void shouldExecuteFullLifecycle() throws Exception {
        MaintenanceDto createDto = new MaintenanceDto(
                "Lifecycle Test: Fix Window",
                "Window is cracked.",
                MaintenanceCategory.BUILDING,
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
        MaintenanceDto requestDto = new MaintenanceDto(
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

    @Test
    @DisplayName("Should filter by status 'active' and return OPEN and IN_PROGRESS records")
    void getMaintenances_withActiveStatus_shouldReturnOpenAndInProgress() throws Exception {
        MaintenanceDto openDto = new MaintenanceDto("Open Task Filter Active", "Desc", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(1));
        MaintenanceDto completedDto = new MaintenanceDto("Completed Task Filter Active", "Desc", MaintenanceCategory.HVAC, ZonedDateTime.now().plusDays(3));

        MvcResult openResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult completedResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completedDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID openId = objectMapper.readValue(openResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID completedId = objectMapper.readValue(completedResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        MaintenanceUpdateDto completeUpdate = new MaintenanceUpdateDto(null, null, null, MaintenanceStatus.COMPLETED);
        mockMvc.perform(patch("/api/v1/maintenances/{publicId}", completedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeUpdate)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/v1/maintenances")
                        .param("status", "active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert responseBody.contains(openId.toString()) : "Active filter should include OPEN status record";
        assert !responseBody.contains(completedId.toString()) : "Active filter should not include COMPLETED status record";

        maintenanceRepository.findByPublicId(openId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(completedId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should filter by status 'inactive' and return COMPLETED and CANCELED records")
    void getMaintenances_withInactiveStatus_shouldReturnCompletedAndCanceled() throws Exception {
        MaintenanceDto activeDto = new MaintenanceDto("Active Task Inactive", "Desc", MaintenanceCategory.BUILDING, ZonedDateTime.now().plusDays(1));
        MaintenanceDto inactiveDto = new MaintenanceDto("Inactive Task Inactive", "Desc", MaintenanceCategory.GARDENING, ZonedDateTime.now().plusDays(2));

        MvcResult activeResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult inactiveResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID activeId = objectMapper.readValue(activeResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID inactiveId = objectMapper.readValue(inactiveResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        MaintenanceUpdateDto completeUpdate = new MaintenanceUpdateDto(null, null, null, MaintenanceStatus.COMPLETED);
        mockMvc.perform(patch("/api/v1/maintenances/{publicId}", inactiveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeUpdate)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/v1/maintenances")
                        .param("status", "inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert responseBody.contains(inactiveId.toString()) : "Inactive filter should include COMPLETED status record";
        assert !responseBody.contains(activeId.toString()) : "Inactive filter should not include OPEN status record";

        maintenanceRepository.findByPublicId(activeId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(inactiveId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should filter by specific status value")
    void getMaintenances_withSpecificStatus_shouldReturnOnlyThatStatus() throws Exception {
        MaintenanceDto openDto = new MaintenanceDto("Open Specific Status", "Desc", MaintenanceCategory.SECURITY, ZonedDateTime.now().plusDays(1));
        MaintenanceDto inProgressDto = new MaintenanceDto("In Progress Specific Status", "Desc", MaintenanceCategory.FURNITURE, ZonedDateTime.now().plusDays(2));

        MvcResult openResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult inProgressResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inProgressDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID openId = objectMapper.readValue(openResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID inProgressId = objectMapper.readValue(inProgressResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.status != 'OPEN')]").isEmpty());

        maintenanceRepository.findByPublicId(openId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(inProgressId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should filter by category")
    void getMaintenances_withCategory_shouldReturnOnlyMatchingCategory() throws Exception {
        MaintenanceDto electricalDto = new MaintenanceDto("Electrical Category Task Filter", "Desc", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(1));
        MaintenanceDto plumbingDto = new MaintenanceDto("Plumbing Category Task Filter", "Desc", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(2));

        MvcResult electricalResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electricalDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult plumbingResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plumbingDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID electricalId = objectMapper.readValue(electricalResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID plumbingId = objectMapper.readValue(plumbingResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("category", "ELECTRICAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.category != 'ELECTRICAL')]").isEmpty());

        maintenanceRepository.findByPublicId(electricalId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(plumbingId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should filter by category case insensitive")
    void getMaintenances_withCategoryCaseInsensitive_shouldReturnMatchingCategory() throws Exception {
        MaintenanceDto hvacDto = new MaintenanceDto("HVAC Case Task Filter", "Desc", MaintenanceCategory.HVAC, ZonedDateTime.now().plusDays(1));

        MvcResult hvacResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hvacDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID hvacId = objectMapper.readValue(hvacResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("category", "hvac"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category").value("HVAC"));

        maintenanceRepository.findByPublicId(hvacId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should search by term in title")
    void getMaintenances_withSearchTerm_shouldFindByTitle() throws Exception {
        MaintenanceDto searchDto = new MaintenanceDto("Unique Searchable Title 123ABC", "Some description", MaintenanceCategory.BUILDING, ZonedDateTime.now().plusDays(1));
        MaintenanceDto otherDto = new MaintenanceDto("Other Title XYZ", "Other description", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(2));

        MvcResult searchResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult otherResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID searchId = objectMapper.readValue(searchResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID otherId = objectMapper.readValue(otherResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        MvcResult result = mockMvc.perform(get("/api/v1/maintenances")
                        .param("search", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert responseBody.contains(searchId.toString()) : "Search should find record with matching title";

        maintenanceRepository.findByPublicId(searchId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(otherId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should search by term case insensitive")
    void getMaintenances_withSearchTermCaseInsensitive_shouldFindMatchingRecords() throws Exception {
        MaintenanceDto caseDto = new MaintenanceDto("CaseInsensitiveTestTaskFilter", "Description", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(1));

        MvcResult caseResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID caseId = objectMapper.readValue(caseResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("search", "caseinsensitive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("CaseInsensitiveTestTaskFilter"));

        maintenanceRepository.findByPublicId(caseId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should return empty when search term has no matches")
    void getMaintenances_withNoMatchingSearchTerm_shouldReturnEmpty() throws Exception {
        MaintenanceDto dto = new MaintenanceDto("Some Task Empty Search", "Description", MaintenanceCategory.HVAC, ZonedDateTime.now().plusDays(1));

        MvcResult dtoResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID dtoId = objectMapper.readValue(dtoResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("search", "xyznonexistent123search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        maintenanceRepository.findByPublicId(dtoId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should return all records when search is empty or null")
    void getMaintenances_withEmptyOrNullSearch_shouldReturnAll() throws Exception {
        MaintenanceDto dto1 = new MaintenanceDto("Task 1 Empty Search Filter", "Desc", MaintenanceCategory.BUILDING, ZonedDateTime.now().plusDays(1));
        MaintenanceDto dto2 = new MaintenanceDto("Task 2 Empty Search Filter", "Desc", MaintenanceCategory.GARDENING, ZonedDateTime.now().plusDays(2));

        MvcResult dto1Result = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult dto2Result = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID dto1Id = objectMapper.readValue(dto1Result.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID dto2Id = objectMapper.readValue(dto2Result.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        maintenanceRepository.findByPublicId(dto1Id).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(dto2Id).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should paginate results correctly")
    void getMaintenances_withPagination_shouldReturnPaginatedResults() throws Exception {
        java.util.List<UUID> createdIds = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MaintenanceDto dto = new MaintenanceDto("Pagination Task Filter " + i, "Desc", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(i));
            MvcResult result = mockMvc.perform(post("/api/v1/maintenances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();
            UUID id = objectMapper.readValue(result.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
            createdIds.add(id);
        }

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));

        for (UUID id : createdIds) {
            maintenanceRepository.findByPublicId(id).ifPresent(maintenanceRepository::delete);
        }
    }

    @Test
    @DisplayName("Should return empty page for out of bounds page")
    void getMaintenances_withOutOfBoundsPage_shouldReturnEmptyPage() throws Exception {
        MaintenanceDto dto = new MaintenanceDto("Single Pagination Task Filter Bounds", "Desc", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(1));

        MvcResult dtoResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID dtoId = objectMapper.readValue(dtoResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        mockMvc.perform(get("/api/v1/maintenances")
                        .param("page", "100")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        maintenanceRepository.findByPublicId(dtoId).ifPresent(maintenanceRepository::delete);
    }

    @Test
    @DisplayName("Should combine status, category and search filters")
    void getMaintenances_withCombinedFilters_shouldReturnMatchingRecords() throws Exception {
        MaintenanceDto targetDto = new MaintenanceDto("Target Combined Task ABC Filter", "Description for target", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(1));
        MaintenanceDto otherDto = new MaintenanceDto("Other Combined Task Filter", "Description", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(2));

        MvcResult targetResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetDto)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult otherResult = mockMvc.perform(post("/api/v1/maintenances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherDto)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID targetId = objectMapper.readValue(targetResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();
        UUID otherId = objectMapper.readValue(otherResult.getResponse().getContentAsString(), MaintenanceResponseDto.class).id();

        MvcResult result = mockMvc.perform(get("/api/v1/maintenances")
                        .param("status", "OPEN")
                        .param("category", "ELECTRICAL")
                        .param("search", "Target"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert responseBody.contains(targetId.toString()) : "Combined filters should return matching record";

        maintenanceRepository.findByPublicId(targetId).ifPresent(maintenanceRepository::delete);
        maintenanceRepository.findByPublicId(otherId).ifPresent(maintenanceRepository::delete);
    }
}
