package com.unnamed.conecta_reparo.dto;

import com.unnamed.conecta_reparo.entity.MaintenanceCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceResponseDto(
        UUID id,
        String title,
        String description,
        MaintenanceCategory category,
        LocalDateTime scheduledDate,
        com.unnamed.conecta_reparo.entity.MaintenanceStatus completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}