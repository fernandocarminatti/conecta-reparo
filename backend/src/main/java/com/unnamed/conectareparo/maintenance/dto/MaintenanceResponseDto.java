package com.unnamed.conectareparo.maintenance.dto;

import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * <p>Data Transfer Object for {@link com.unnamed.conectareparo.maintenance.entity.Maintenance} http response.</p>
 */
@Schema(description = "Data Transfer Object for maintenance response")
public record MaintenanceResponseDto(
        @Schema(description = "Unique identifier of the maintenance request", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Title of the maintenance request", example = "Vazamento na pia da cozinha")
        String title,
        @Schema(description = "Detailed description of the maintenance issue", example = "Há um vazamento constante na pia da cozinha que precisa ser reparado.")
        String description,
        @Schema(description = "Category of the maintenance issue", example = "PLUMBING")
        MaintenanceCategory category,
        @Schema(description = "Scheduled date and time for the maintenance in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime scheduledDate,
        @Schema(description = "Current status of the maintenance request", example = "IN_PROGRESS")
        MaintenanceStatus status,
        @Schema(description = "Timestamp when the maintenance request was created in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime createdAt,
        @Schema(description = "Timestamp when the maintenance request was last updated in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime updatedAt
) {
}