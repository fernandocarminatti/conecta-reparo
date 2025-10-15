package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for {@link com.unnamed.conectareparo.entity.Pledge} http response.
 */
@Schema(description = "Data Transfer Object for Pledge response.")
public record PledgeResponseDto(
        @Schema(description = "Unique identifier of the pledge.", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Name of the volunteer making the pledge.", example = "John Doe")
        String volunteerName,
        @Schema(description = "Contact information of the volunteer.", example = "Email: john@doe.com or Phone: +123456789")
        String volunteerContact,
        @Schema(description = "Detailed description of the pledge.", example = "Eu posso ajudar com o conserto das portas.")
        String description,
        @Schema(description = "Type of the pledge.", example = "LABOR OR MATERIAL")
        PledgeType type,
        @Schema(description = "Current system status of the Pledge", example = "OFFERED (Default), PENDING, REJECTED, COMPLETED, CANCELED")
        PledgeStatus status,
        @Schema(description = "Timestamp when the pledge was created in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime createdAt,
        @Schema(description = "Timestamp when the pledge was last updated in ISO 8601 format.", example = "2025-10-10T10:10:10Z")
        ZonedDateTime updatedAt
) {
}