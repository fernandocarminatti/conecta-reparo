package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceResponseDto(
        UUID id,
        String title,
        String description,
        MaintenanceCategory category,
        LocalDateTime scheduledDate,
        com.unnamed.conectareparo.entity.MaintenanceStatus completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}