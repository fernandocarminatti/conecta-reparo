package com.unnamed.conectareparo.maintenanceaction.dto;

import com.unnamed.conectareparo.maintenanceaction.entity.ActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Data Transfer Object for creating {@link com.unnamed.conectareparo.entity.MaintenanceAction} via http request.
 */
@Schema(description = "Data Transfer Object for creating a new maintenance action")
public record MaintenanceActionDto(
        @Schema(description = "Name of the person who executed the maintenance action", example = "João Silva")
        @NotBlank(message = "Executed by must not be blank")
        @Size(min = 3, max = 100, message = "Executed by must be between 3 and 100 characters long")
        String executedBy,
        @Schema(description = "Date and time when the maintenance action started in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        @NotNull(message = "Start date must not be null")
        ZonedDateTime startDate,
        @Schema(description = "Date and time when the maintenance action was completed in ISO 8601 format.", example = "2025-10-10T12:10:10Z")
        @NotNull(message = "Completion date must not be null")
        ZonedDateTime completionDate,
        @Schema(description = "Detailed description of the maintenance action", example = "Realizado o reparo do vazamento na pia da cozinha, substituindo o cano danificado.")
        @NotBlank(message = "Description must contain at least some text")
        @Size(min = 10, max = 2000, message = "Description must be at least 10 characters long")
        String actionDescription,
        @Schema(description = "List of materials used during the maintenance action",
                example = "[" +
                        "{\"" +
                        "itemName\": \"Cano PVC de 1 polegada\"," +
                        " \"quantity\": 1," +
                        " \"unitOfMeasure\": \"un\"}," +
                        " {\"itemName\": \"Selante para canos\"," +
                        " \"quantity\": 2," +
                        " \"unitOfMeasure\": \"un\"}" +
                        "]")
        @Valid
        List<MaterialDto> materialsUsed,
        @Schema(description = "Outcome status of the maintenance action", example = "SUCCESS")
        @NotNull(message = "This action must contain a valid outcome status")
        ActionStatus outcomeStatus
) {
}