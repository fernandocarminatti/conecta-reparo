package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionOutcomeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Data Transfer Object for {@link MaintenanceAction} Creation
 * @param executedBy the name of the person who executed the action
 * @param startDate the date and time when the action started
 * @param completionDate the date and time when the action was completed
 * @param actionDescription a description of the maintenance action
 * @param materialsUsed a list of materials used during the action
 * @param outcomeStatus the outcome status of the action
 */
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