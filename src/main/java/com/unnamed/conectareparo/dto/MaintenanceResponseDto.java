package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for {@link Maintenance} Response
 * @param id id of the maintenance, generated automatically
 * @param title title of the maintenance
 * @param description detailed description of the maintenance
 * @param category category of the maintenance
 * @param scheduledDate date when the maintenance is scheduled
 * @param status current status of the maintenance
 * @param createdAt date and time when the maintenance was created
 * @param updatedAt date and time when the maintenance was last updated
 */
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