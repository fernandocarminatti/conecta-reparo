package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
/**
 * Data Transfer Object for {@link Maintenance} Creation
 * @param title title of the maintenance
 * @param description detailed description of the maintenance
 * @param category category of the maintenance
 * @param scheduledDate date when the maintenance is scheduled
 */
public record NewMaintenanceRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        MaintenanceCategory category,
        @NotNull
        @FutureOrPresent
        ZonedDateTime scheduledDate
        ) {
}