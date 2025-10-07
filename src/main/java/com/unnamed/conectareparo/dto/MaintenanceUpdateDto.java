package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import jakarta.validation.constraints.Size;

public record MaintenanceUpdateDto (
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters.")
        String title,
        @Size(max = 3000, message = "Description must not exceed 3000 characters.")
        String description,
        MaintenanceCategory category,
        MaintenanceStatus status
){
}