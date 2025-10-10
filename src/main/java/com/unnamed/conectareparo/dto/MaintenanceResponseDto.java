package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MaintenanceResponseDto(
        UUID id,
        String title,
        String description,
        MaintenanceCategory category,
        ZonedDateTime scheduledDate,
        com.unnamed.conectareparo.entity.MaintenanceStatus completed,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}