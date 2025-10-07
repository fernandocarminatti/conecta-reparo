package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.MaintenanceCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NewMaintenanceRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        MaintenanceCategory category,
        @NotNull
        @FutureOrPresent
        LocalDateTime scheduledDate
        ) {
}