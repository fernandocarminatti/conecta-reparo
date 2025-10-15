package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Data Transfer Object for creating {@link com.unnamed.conectareparo.entity.Pledge} via http request.
 */
@Schema(description = "Data Transfer Object for creating a new pledge")
public record NewPledgeRequestDto(
        @Schema(description = "ID of the maintenance request associated with this pledge", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID maintenanceId,
        @Schema(description = "Name of the volunteer making the pledge", example = "John Doe")
        @Size(min = 5, max = 100, message = "Name must be between 5 and 100 characters.")
        @NotBlank(message = "Must provide volunteer name.")
        String volunteerName,
        @Schema(description = "Contact information of the volunteer", example = "Email: john@doe.com or Phone: +123456789")
        @Size(min = 5, max = 30, message = "Contact must be between 5 and 30 characters.")
        @NotBlank(message = "Must provide volunteer contact.")
        String volunteerContact,
        @Schema(description = "Detailed description of the pledge", example = "Eu posso ajudar com o reparo do telhado.")
        @Size(max = 3000, message = "Description must not exceed 3000 characters.")
        @NotBlank(message = "Must provide a description.")
        String description,
        @Schema(description = "Type of pledge being made", example = "LABOR OR MATERIAL")
        PledgeType type,
        @Schema(description = "Current status of the pledge", example = "OFFERED (Default), PENDING, REJECTED, COMPLETED, CANCELED")
        PledgeStatus status
) {
}