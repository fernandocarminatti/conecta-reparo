package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Data Transfer Object for updating {@link com.unnamed.conectareparo.entity.MaintenanceAction} via http request.
 */
@Schema(description = "Data Transfer Object for updating a maintenance action")
public record MaintenanceActionUpdateDto(
        @Schema(description = "The name of the person who executed the action.", example = "Jane Smith")
        @NotBlank(message = "Executed by must not be blank")
        @Size(min = 3, max = 100, message = "Executed by must be between 3 and 100 characters long")
        String executedBy,
        @Schema(description = "The start date and time of the maintenance action in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        @NotNull(message = "Start date must not be null")
        ZonedDateTime startDate,
        @NotNull(message = "Completion date must not be null")
        ZonedDateTime completionDate,
        @NotBlank(message = "Description must contain at least some text")
        @Size(min = 10, max = 2000, message = "Description must be at least 10 characters long")
        String actionDescription,
        @Valid
        List<MaterialDto> materialsUsed,
        @NotNull(message = "This action must contain a valid outcome status")
        ActionStatus outcomeStatus
) {
}