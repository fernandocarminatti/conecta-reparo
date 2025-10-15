package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
/**
 * Data Transfer Object for creating {@link com.unnamed.conectareparo.entity.Maintenance} via http request.
 */
@Schema(description = "Data Transfer Object for creating a new maintenance request")
public record NewMaintenanceRequestDto(
        @Schema(description = "Title of the maintenance request", example = "Vazamento na pia da cozinha")
        @NotBlank
        String title,
        @Schema(description = "Detailed description of the maintenance issue", example = "Há um vazamento constante na pia da cozinha que precisa ser reparado.")
        @NotBlank
        String description,
        @Schema(description = "Category of the maintenance issue", example = "PLUMBING")
        @NotNull
        MaintenanceCategory category,
        @Schema(description = "Scheduled date and time for the maintenance in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        @NotNull
        ZonedDateTime scheduledDate
        ) {
}