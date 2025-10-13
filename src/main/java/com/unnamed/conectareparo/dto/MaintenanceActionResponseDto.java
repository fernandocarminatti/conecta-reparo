package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionOutcomeStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for {@link MaintenanceAction} Response
 * @param publicId public facing identifier as UUID
 * @param executedBy the name of the person who executed the action
 * @param startDate the date and time when the action started
 * @param completionDate the date and time when the action was completed
 * @param actionDescription a description of the maintenance action
 * @param materialsUsed a list of materials used during the action
 * @param outcomeStatus the outcome status of the action
 * @param createdAt the date and time when the action record was created
 */
public record MaintenanceActionResponseDto(
        UUID publicId,
        String executedBy,
        ZonedDateTime startDate,
        ZonedDateTime completionDate,
        String actionDescription,
        List<ActionMaterialResponseDto> materialsUsed,
        ActionOutcomeStatus outcomeStatus,
        ZonedDateTime createdAt
) {
}