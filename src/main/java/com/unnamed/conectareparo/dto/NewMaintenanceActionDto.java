package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionOutcomeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record NewMaintenanceActionDto(
        @NotBlank(message = "Executed by must not be blank")
        String executedBy,
        @NotNull(message = "Start date must not be null")
        ZonedDateTime startDate,
        @NotNull(message = "Completion date must not be null")
        ZonedDateTime completionDate,
        @NotBlank(message = "Description must contain at least some text")
        String actionDescription,
        @Valid
        List<NewActionMaterialDto> materialsUsed,
        Map<String, Object> categorySpecificData,
        @NotNull(message = "This action must contain a valid outcome status")
        ActionOutcomeStatus outcomeStatus,
        String finalRemarks
) {
}