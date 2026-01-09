package com.unnamed.conectareparo.maintenance.dto;

import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for {@link com.unnamed.conectareparo.maintenance.entity.Maintenance} update http request.
 */
@Schema(description = "Data Transfer Object for updating a maintenance request")
public record MaintenanceUpdateDto (
        @Schema(description = "Title of the maintenance request", example = "Vazamento na pia da cozinha - Atualizado")
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters.")
        String title,
        @Schema(description = "Detailed description of the maintenance issue", example = "O vazamento na pia da cozinha foi agravado e precisa de atenção urgente.")
        @Size(max = 3000, message = "Description must not exceed 3000 characters.")
        String description,
        @Schema(description = "Category of the maintenance issue", example = "PLUMBING")
        MaintenanceCategory category,
        @Schema(description = "Current status of the maintenance request", example = "IN_PROGRESS")
        MaintenanceStatus status
){
}