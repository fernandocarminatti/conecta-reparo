package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Data Transfer Object for {@link Pledge} Creation
 * @param maintenanceId public facing id of the maintenance as UUID
 * @param volunteerName name of the volunteer making the pledge
 * @param volunteerContact contact information of the volunteer (e.g., email or phone)
 * @param description detailed description of the pledge
 * @param type type of the pledge
 * @param status current status of the pledge
 */
public record NewPledgeRequestDto(
        @NotNull
        UUID maintenanceId,
        @Size(min = 5, max = 100, message = "Name must be between 5 and 100 characters.")
        @NotBlank(message = "Must provide volunteer name.")
        String volunteerName,
        @Size(min = 5, max = 30, message = "Contact must be between 5 and 30 characters.")
        @NotBlank(message = "Must provide volunteer contact.")
        String volunteerContact,
        @Size(max = 3000, message = "Description must not exceed 3000 characters.")
        @NotBlank(message = "Must provide a description.")
        String description,
        PledgeType type,
        PledgeStatus status
) {
}