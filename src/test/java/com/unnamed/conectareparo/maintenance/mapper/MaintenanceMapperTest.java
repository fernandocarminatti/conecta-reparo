package com.unnamed.conectareparo.maintenance.mapper;

import com.unnamed.conectareparo.maintenance.dto.MaintenanceDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceMapperTest {
    private MaintenanceMapper mapper;

    private Maintenance maintenanceEntity;
    private MaintenanceDto maintenanceDto;
    private ZonedDateTime now;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        mapper = new MaintenanceMapper();
        now = ZonedDateTime.parse("2025-10-08T10:00:00Z");
        publicId = UUID.randomUUID();

        maintenanceDto = new MaintenanceDto(
                "Leaking Pipe",
                "Water leaking from the pipe under the main sink.",
                MaintenanceCategory.PLUMBING,
                now.plusDays(1)
        );
        maintenanceEntity = new Maintenance(
                "Power Outage in Wing B",
                "Circuit breaker for Wing B keeps tripping.",
                MaintenanceCategory.ELECTRICAL,
                now.plusHours(2)
        );
        ReflectionTestUtils.setField(maintenanceEntity, "publicId", publicId);
        ReflectionTestUtils.setField(maintenanceEntity, "status", MaintenanceStatus.OPEN);
        ReflectionTestUtils.setField(maintenanceEntity, "createdAt", now);
        ReflectionTestUtils.setField(maintenanceEntity, "updatedAt", now);
    }

    @Test
    @DisplayName("Should correctly map MaintenanceDto to a new Maintenance Entity")
    void toEntity_shouldMapAllFieldsFromRequestDto() {
        Maintenance resultEntity = mapper.toEntity(maintenanceDto);

        assertAll(
                () -> assertNotNull(resultEntity),
                () -> assertEquals("Leaking Pipe", resultEntity.getTitle()),
                () -> assertEquals("Water leaking from the pipe under the main sink.", resultEntity.getDescription()),
                () -> assertEquals(MaintenanceCategory.PLUMBING, resultEntity.getCategory()),
                () -> assertEquals(now.plusDays(1), resultEntity.getScheduledDate()),
                () -> assertNotNull(resultEntity.getPublicId()),
                () -> assertEquals(MaintenanceStatus.OPEN, resultEntity.getStatus())
        );
    }

    @Test
    @DisplayName("Should correctly map Maintenance Entity to MaintenanceResponseDto")
    void toResponseDto_shouldMapAllFieldsFromEntity() {
        MaintenanceResponseDto resultDto = mapper.toResponseDto(maintenanceEntity);

        assertAll(
                () -> assertNotNull(resultDto),
                () -> assertEquals(maintenanceEntity.getPublicId(), resultDto.id()),
                () -> assertEquals("Power Outage in Wing B", resultDto.title()),
                () -> assertEquals("Circuit breaker for Wing B keeps tripping.", resultDto.description()),
                () -> assertEquals(MaintenanceCategory.ELECTRICAL, resultDto.category()),
                () -> assertEquals(now.plusHours(2), resultDto.scheduledDate()),
                () -> assertEquals(MaintenanceStatus.OPEN, resultDto.status()),
                () -> assertEquals(now, resultDto.createdAt()),
                () -> assertEquals(now, resultDto.updatedAt())
        );
    }

    @Test
    @DisplayName("Should return null when mapping a null Maintenance Entity to DTO")
    void toResponseDto_whenEntityIsNull_shouldReturnNull() {
        assertThrows(NullPointerException.class, () -> mapper.toResponseDto(null));
    }
}