package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MaintenanceResponseDto(
        UUID id,
        String title,
        String description,
        MaintenanceCategory category,
        ZonedDateTime scheduledDate,
        MaintenanceStatus status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}