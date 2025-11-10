package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <p>Data Transfer Object for {@link com.unnamed.conectareparo.entity.MaintenanceAction} http response.</p>
 */
@Schema(description = "Data Transfer Object for Maintenance Action Response")
public record MaintenanceActionResponseDto(
        @Schema(description = "Unique identifier of the maintenance action", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Identifier of the associated maintenance", example = "João Silva")
        String executedBy,
        @Schema(description = "Date and time when the maintenance action started in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime startDate,
        @Schema(description = "Date and time when the maintenance action was completed in ISO 8601 format.", example = "2025-10-10T12:10:10Z")
        ZonedDateTime completionDate,
        @Schema(description = "Detailed description of the maintenance action", example = "Realizado o reparo do vazamento na pia da cozinha, substituindo o cano danificado.")
        String actionDescription,
        @Schema(description = "List of materials used during the maintenance action",
                example = "[" +
                        "{\"" +
                        "materialName\": \"Cano PVC de 1 polegada\"," +
                        " \"quantityUsed\": 1}," +
                        " {\"materialName\": \"Selante\"," +
                        " \"quantityUsed\": 2}" +
                        "]")
        List<MaterialResponseDto> materialsUsed,
        @Schema(description = "Outcome status of the maintenance action", example = "SUCCESS")
        ActionStatus outcomeStatus,
        @Schema(description = "Date and time when the maintenance action was created in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime createdAt
) {
}