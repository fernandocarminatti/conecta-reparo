package com.unnamed.conectareparo.maintenance.controller;

import com.unnamed.conectareparo.maintenance.dto.MaintenanceDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.maintenance.service.MaintenanceService;
import com.unnamed.conectareparo.common.exception.ErrorResponse;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/maintenances")
@Tag(name = "Maintenance", description = "Endpoints for managing maintenances")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(
        summary = "Creates a new maintenance.",
        description = "Registers a new maintenance in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Maintenance created successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceResponseDto.class)
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
    @PostMapping()
    public ResponseEntity<MaintenanceResponseDto> createMaintenance(@Valid @RequestBody MaintenanceDto maintenanceDTO) {
        MaintenanceResponseDto createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO);
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMaintenance.id())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(resourceLocation).body(createdMaintenance);
    }

    @Operation(
        summary = "Retrieves all maintenances.",
        description = "Fetches a paginated list of all maintenances in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Maintenances retrieved successfully.",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = MaintenanceResponseDto.class))
        )
    )
    @GetMapping
    public ResponseEntity<Page<MaintenanceResponseDto>> getMaintenances(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @ParameterObject Pageable pageable) {
        Page<MaintenanceResponseDto> foundMaintenances = maintenanceService.getAllMaintenances(status, category, search, pageable);
        return ResponseEntity.ok(foundMaintenances);
    }

    @Operation(
        summary = "Retrieve a maintenance by its public ID.",
        description = "Retrieves the details of a specific maintenance using its public UUID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Maintenance retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceResponseDto.class)
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
            description = "Invalid ID format.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{publicId}")
    public ResponseEntity<MaintenanceResponseDto> getMaintenanceByPublicId(@PathVariable UUID publicId) {
        MaintenanceResponseDto foundMaintenance = maintenanceService.getMaintenanceByPublicId(publicId);
        return ResponseEntity.ok(foundMaintenance);
    }

    @Operation(
        summary = "Update a maintenance by its public ID.",
        description = "Updates the details of an existing maintenance identified by its ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Maintenance updated successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MaintenanceResponseDto.class)
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
        )
    })
    @PatchMapping("/{publicId}")
    public ResponseEntity<MaintenanceResponseDto> updateMaintenance(
            @PathVariable UUID publicId,
            @Valid @RequestBody MaintenanceUpdateDto updateDto){
        MaintenanceResponseDto updatedMaintenance = maintenanceService.updateMaintenance(publicId, updateDto);
        return ResponseEntity.ok(updatedMaintenance);
    }
}