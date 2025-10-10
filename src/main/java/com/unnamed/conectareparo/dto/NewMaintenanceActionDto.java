package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionOutcomeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;

public record NewMaintenanceActionDto(
        @NotBlank(message = "Executed by must not be blank")
        @Size(min = 3, max = 100, message = "Executed by must be between 3 and 100 characters long")
        String executedBy,
        @NotNull(message = "Start date must not be null")
        ZonedDateTime startDate,
        @NotNull(message = "Completion date must not be null")
        ZonedDateTime completionDate,
        @NotBlank(message = "Description must contain at least some text")
        @Size(min = 10, max = 2000, message = "Description must be at least 10 characters long")
        String actionDescription,
        @Valid
        List<NewActionMaterialDto> materialsUsed,
        @NotNull(message = "This action must contain a valid outcome status")
        ActionOutcomeStatus outcomeStatus
) {
}