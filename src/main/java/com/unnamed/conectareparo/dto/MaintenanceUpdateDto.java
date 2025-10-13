package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for {@link Maintenance} Update
 * @param title title of the maintenance
 * @param description detailed description of the maintenance
 * @param category category of the maintenance
 * @param status current status of the maintenance
 */
public record MaintenanceUpdateDto (
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters.")
        String title,
        @Size(max = 3000, message = "Description must not exceed 3000 characters.")
        String description,
        MaintenanceCategory category,
        MaintenanceStatus status
){
}