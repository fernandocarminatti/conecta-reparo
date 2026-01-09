package com.unnamed.conectareparo.mapper;

import com.unnamed.conectareparo.dto.PledgeDto;
import com.unnamed.conectareparo.dto.PledgeResponseDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.Pledge;
import com.unnamed.conectareparo.entity.PledgeCategory;
import com.unnamed.conectareparo.entity.PledgeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PledgeMapperTest {
    private PledgeMapper mapper;

    private Maintenance parentMaintenance;
    private Pledge pledgeEntity;
    private PledgeDto newPledgeRequestDto;
    private ZonedDateTime now;
    private UUID pledgePublicId;

    @BeforeEach
    void setUp() {
        mapper = new PledgeMapper();
        now = ZonedDateTime.parse("2025-10-08T10:00:00Z");
        pledgePublicId = UUID.randomUUID();
        parentMaintenance = new Maintenance();
        newPledgeRequestDto = new PledgeDto(
                UUID.randomUUID(),
                "Jane Doe",
                "jane.doe@example.com",
                "I can donate paint.",
                PledgeCategory.MATERIAL,
                PledgeStatus.OFFERED
        );

        pledgeEntity = new Pledge(
                parentMaintenance,
                "John Doe",
                "john.doe@example.com",
                "I can help on Saturday.",
                PledgeCategory.LABOR
        );
        ReflectionTestUtils.setField(pledgeEntity, "publicId", pledgePublicId);
        ReflectionTestUtils.setField(pledgeEntity, "status", PledgeStatus.PENDING);
        ReflectionTestUtils.setField(pledgeEntity, "createdAt", now);
        ReflectionTestUtils.setField(pledgeEntity, "updatedAt", now);
    }

    @Test
    @DisplayName("Should correctly map PledgeDto to a new Pledge Entity")
    void toEntity_shouldMapAllFieldsFromRequestDto() {
        Pledge resultEntity = mapper.toEntity(parentMaintenance, newPledgeRequestDto);

        assertAll(
                () -> assertNotNull(resultEntity),
                () -> assertSame(parentMaintenance, resultEntity.getMaintenanceId()),
                () -> assertEquals("Jane Doe", resultEntity.getVolunteerName()),
                () -> assertEquals("jane.doe@example.com", resultEntity.getVolunteerContact()),
                () -> assertEquals("I can donate paint.", resultEntity.getDescription()),
                () -> assertEquals(PledgeCategory.MATERIAL, resultEntity.getType()),
                () -> assertNotNull(resultEntity.getPublicId()),
                () -> assertEquals(PledgeStatus.OFFERED, resultEntity.getStatus())
        );
    }

    @Test
    @DisplayName("Should correctly map Pledge Entity to PledgeResponseDto")
    void toResponseDto_shouldMapAllFieldsFromEntity() {
        PledgeResponseDto resultDto = mapper.toResponseDto(pledgeEntity);

        assertAll(
                () -> assertNotNull(resultDto),
                () -> assertEquals(pledgeEntity.getPublicId(), resultDto.id()),
                () -> assertEquals("John Doe", resultDto.volunteerName()),
                () -> assertEquals("john.doe@example.com", resultDto.volunteerContact()),
                () -> assertEquals("I can help on Saturday.", resultDto.description()),
                () -> assertEquals(PledgeCategory.LABOR, resultDto.type()),
                () -> assertEquals(PledgeStatus.PENDING, resultDto.status()),
                () -> assertEquals(now, resultDto.createdAt()),
                () -> assertEquals(now, resultDto.updatedAt())
        );
    }

    @Test
    @DisplayName("Should return throw NullPointerException when mapping a null Pledge Entity to DTO")
    void toResponseDto_whenEntityIsNull_shouldReturnNull() {
        assertThrows(NullPointerException.class, () -> mapper.toResponseDto(null));
    }
}