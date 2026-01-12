package com.unnamed.conectareparo.pledge.controller;

import com.unnamed.conectareparo.pledge.dto.PledgeDto;
import com.unnamed.conectareparo.pledge.dto.PledgeResponseDto;
import com.unnamed.conectareparo.pledge.dto.PledgeUpdateDto;
import com.unnamed.conectareparo.common.exception.ErrorResponse;
import com.unnamed.conectareparo.pledge.service.PledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pledges")
@Tag(name = "Pledge", description = "Endpoints for managing pledges")
public class PledgeController {

    private final PledgeService pledgeService;

    public PledgeController(PledgeService pledgeService) {
        this.pledgeService = pledgeService;
    }

    @Operation(
        summary = "Creates a new pledge.",
        description = "Registers a new pledge in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Pledge created successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PledgeResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping
    public ResponseEntity<PledgeResponseDto> createPledge(@Valid @RequestBody PledgeDto pledgeRequestDto) {
        PledgeResponseDto pledgeResponseDto = pledgeService.createPledge(pledgeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pledgeResponseDto);
    }

    @Operation(
        summary = "Retrieves all pledges.",
        description = "Fetches a paginated list of all pledges in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Pledges retrieved successfully.",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = PledgeResponseDto.class))
        )
    )
    @GetMapping
    public ResponseEntity<Page<PledgeResponseDto>> getAllPledges(@ParameterObject Pageable pageable) {
        Page<PledgeResponseDto> pledges = pledgeService.getAllPledges(pageable);
        return ResponseEntity.ok(pledges);
    }

    @Operation(
        summary = "Retrieve a pledge by its public ID.",
        description = "Retrieves the details of a specific pledge using its public UUID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pledge retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PledgeResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pledge not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{pledgeId}")
    public ResponseEntity<PledgeResponseDto> getPledgeByPublicId(@PathVariable UUID pledgeId) {
        PledgeResponseDto pledge = pledgeService.getPledgeByPublicId(pledgeId);
        return ResponseEntity.ok(pledge);
    }

    @Operation(
        summary = "Retrieves pledges for a specific maintenance ID.",
        description = "Fetches a paginated list of pledges associated with the given maintenance ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pledges retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PledgeResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid maintenance ID.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping(params = "maintenanceId")
    public ResponseEntity<Page<PledgeResponseDto>> getPledgesForMaintenanceId(@RequestParam UUID maintenanceId, @ParameterObject Pageable pageable) {
        Page<PledgeResponseDto> pledges = pledgeService.getPledgesByMaintenanceId(pageable, maintenanceId);
        return ResponseEntity.ok(pledges);
    }

    @Operation(
        summary = "Updates the status of a pledge.",
        description = "Updates the status of an existing pledge identified by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pledge updated successfully.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PledgeResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PatchMapping("/{pledgeId}")
    public ResponseEntity<PledgeResponseDto> updatePledge(
            @PathVariable UUID pledgeId,
            @Valid @RequestBody PledgeUpdateDto pledgeUpdateDto) {
        PledgeResponseDto updatedPledge = pledgeService.updatePledge(pledgeId, pledgeUpdateDto);
        return ResponseEntity.ok(updatedPledge);
    }
}