package com.unnamed.conectareparo.maintenanceaction.mapper;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaterialDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaterialResponseDto;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionMaterial;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionStatus;
import com.unnamed.conectareparo.maintenanceaction.entity.MaintenanceAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceActionMapperTest {
    private MaintenanceActionMapper mapper;

    private Maintenance maintenance;
    private MaintenanceAction maintenanceAction;
    private MaintenanceActionDto maintenanceActionDto;
    private ZonedDateTime now;
    private UUID maintenanceActionPublicId;

    @BeforeEach
    void setUp() {
        mapper = new MaintenanceActionMapper();

        now = ZonedDateTime.parse("2025-10-08T10:00:00Z");
        maintenanceActionPublicId = UUID.randomUUID();
        maintenance = new Maintenance();

        maintenanceAction = new MaintenanceAction(
                maintenance,
                "John Doe",
                now.minusHours(2),
                now,
                "Replaced the main circuit breaker.",
                ActionStatus.SUCCESS
        );
        ReflectionTestUtils.setField(maintenanceAction, "publicId", maintenanceActionPublicId);
        ReflectionTestUtils.setField(maintenanceAction, "createdAt", now);

        ActionMaterial material1 = new ActionMaterial("Circuit Breaker 50A", BigDecimal.ONE, "unit");
        ActionMaterial material2 = new ActionMaterial("Electrical Tape", BigDecimal.TEN, "meters");
        maintenanceAction.addMaterial(material1);
        maintenanceAction.addMaterial(material2);

        maintenanceActionDto = new MaintenanceActionDto(
                "Jane Doe",
                now.plusDays(1),
                now.plusDays(1).plusHours(4),
                "Scheduled filter cleaning.",
                List.of(new MaterialDto("Air Filter", BigDecimal.ONE, "unit")),
                ActionStatus.SUCCESS
        );
    }

    @Test
    @DisplayName("Should correctly map MaintenanceAction entity to Response DTO")
    void toResponseDto_shouldMapAllFieldsCorrectly() {
        MaintenanceActionResponseDto dto = mapper.toResponseDto(maintenanceAction);

        assertAll(
                () -> assertNotNull(dto),
                () -> assertEquals(maintenanceAction.getPublicId(), dto.id()),
                () -> assertEquals("John Doe", dto.executedBy()),
                () -> assertEquals(now, dto.completionDate()),
                () -> assertEquals("Replaced the main circuit breaker.", dto.actionDescription()),
                () -> assertEquals(ActionStatus.SUCCESS, dto.outcomeStatus()),
                () -> assertEquals(now, dto.createdAt()),
                () -> assertNotNull(dto.materialsUsed()),
                () -> assertEquals(2, dto.materialsUsed().size()),
                () -> assertEquals("Circuit Breaker 50A", dto.materialsUsed().get(0).itemName())
        );
    }

    @Test
    @DisplayName("Should return null when mapping a null MaintenanceAction entity")
    void toResponseDto_whenEntityIsNull_shouldReturnNull() {
        MaintenanceActionResponseDto dto = mapper.toResponseDto(null);

        assertNull(dto);
    }

    @Test
    @DisplayName("Should correctly map MaintenanceActionDto to a new Entity")
    void toEntity_shouldMapAllFieldsCorrectly() {
        MaintenanceAction entity = mapper.toEntity(maintenanceActionDto, maintenance);

        assertAll(
                () -> assertNotNull(entity),
                () -> assertSame(maintenance, entity.getMaintenance()),
                () -> assertEquals("Jane Doe", entity.getExecutedBy()),
                () -> assertEquals(now.plusDays(1).plusHours(4), entity.getCompletionDate()),
                () -> assertEquals(ActionStatus.SUCCESS, entity.getOutcomeStatus()),
                () -> assertNotNull(entity.getMaterialsUsed()),
                () -> assertEquals(1, entity.getMaterialsUsed().size()),
                () -> assertEquals("Air Filter", entity.getMaterialsUsed().get(0).getItemName()),
                () -> assertSame(entity, entity.getMaterialsUsed().get(0).getMaintenanceAction())
        );
    }

    @Test
    @DisplayName("Should return null when mapping a null MaintenanceActionDto")
    void toEntity_whenDtoIsNull_shouldReturnNull() {
        MaintenanceAction entity = mapper.toEntity(null, maintenance);

        assertNull(entity);
    }

    @Test
    @DisplayName("Should correctly map ActionMaterial entity to Response DTO")
    void toMaterialResponseDto_shouldMapAllFields() {
        ActionMaterial material = new ActionMaterial("Test Item", BigDecimal.valueOf(12.5), "kg");
        ReflectionTestUtils.setField(material, "publicId", UUID.randomUUID());

        MaterialResponseDto dto = mapper.toMaterialResponseDto(material);

        assertAll(
                () -> assertEquals(material.getPublicId(), dto.id()),
                () -> assertEquals("Test Item", dto.itemName()),
                () -> assertEquals(BigDecimal.valueOf(12.5), dto.quantity()),
                () -> assertEquals("kg", dto.unitOfMeasure())
        );
    }

    @Test
    @DisplayName("Should correctly map MaterialDto to a new Entity")
    void toMaterialEntity_shouldMapAllFields() {
        MaterialDto dto = new MaterialDto("New Item", BigDecimal.TEN, "box");

        ActionMaterial entity = mapper.toMaterialEntity(dto);

        assertAll(
                () -> assertEquals("New Item", entity.getItemName()),
                () -> assertEquals(BigDecimal.TEN, entity.getQuantity()),
                () -> assertEquals("box", entity.getUnitOfMeasure())
        );
    }
}