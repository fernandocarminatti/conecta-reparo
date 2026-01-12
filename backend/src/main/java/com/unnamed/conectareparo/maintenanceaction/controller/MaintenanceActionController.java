package com.unnamed.conectareparo.maintenanceaction.controller;

import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionUpdateDto;
import com.unnamed.conectareparo.common.exception.ErrorResponse;
import com.unnamed.conectareparo.maintenanceaction.service.MaintenanceActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenances/{maintenancePublicId}/actions")
@Tag(name = "Maintenance Action", description = "Endpoints for managing maintenance actions")
public class MaintenanceActionController {

    private final MaintenanceActionService maintenanceActionService;

    public MaintenanceActionController(MaintenanceActionService maintenanceActionService) {
        this.maintenanceActionService = maintenanceActionService;
    }

    @Operation(
        summary = "Retrieves all maintenance actions.",
        description = "Fetches a list of all maintenance actions across all maintenances."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Maintenance actions retrieved successfully.",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = MaintenanceActionResponseDto.class))
        )
    )
    @GetMapping("/api/v1/actions")
    public ResponseEntity<List<MaintenanceActionResponseDto>> getAllActions() {
        List<MaintenanceActionResponseDto> actionsList = maintenanceActionService.getAllActions();
        return ResponseEntity.ok(actionsList);
    }

    @Operation(
        summary = "Creates a new maintenance action.",
        description = "Registers a new action for a specific maintenance."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Maintenance action created successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceActionResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
    })
    @PostMapping
    public ResponseEntity<MaintenanceActionResponseDto> createMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @Valid @RequestBody MaintenanceActionDto maintenanceActionDto) {
        MaintenanceActionResponseDto responseDto = maintenanceActionService.createMaintenanceAction(maintenancePublicId, maintenanceActionDto);
        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}")
                .buildAndExpand(maintenancePublicId, responseDto.id())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(resourceLocation).body(responseDto);
    }

    @Operation(
        summary = "Retrieves maintenance actions for a specific maintenance ID.",
        description = "Fetches a list of actions associated with the given maintenance ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Maintenance actions retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceActionResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Maintenance not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),

    })
    @GetMapping
    public ResponseEntity<List<MaintenanceActionResponseDto>> getMaintenanceActions(@PathVariable UUID maintenancePublicId) {
        List<MaintenanceActionResponseDto> actionsList = maintenanceActionService.getMaintenanceActions(maintenancePublicId);
        return ResponseEntity.ok(actionsList);
    }

    @Operation(
        summary = "Retrieves a specific maintenance action by its ID.",
        description = "Fetches the details of a specific action using its public UUID within a maintenance."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Maintenance action retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceActionResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Maintenance or action not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),

    })
    @GetMapping("/{actionPublicId}")
    public ResponseEntity<MaintenanceActionResponseDto> getSingleMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @PathVariable UUID actionPublicId) {
        MaintenanceActionResponseDto actionDto = maintenanceActionService.getSingleMaintenanceAction(maintenancePublicId, actionPublicId);
        return ResponseEntity.ok(actionDto);
    }

    @Operation(
        summary = "Updates a maintenance action by its ID.",
        description = "Updates the details of an existing action identified by its ID within a maintenance."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Maintenance action updated successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceActionResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Maintenance or action not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
    })
    @PutMapping("/{actionPublicId}")
    public ResponseEntity<MaintenanceActionResponseDto> updateMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @PathVariable UUID actionPublicId,
            @Valid @RequestBody MaintenanceActionUpdateDto updatedActionDto) {
        MaintenanceActionResponseDto updatedDto = maintenanceActionService.updateMaintenanceAction(maintenancePublicId, actionPublicId, updatedActionDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
}