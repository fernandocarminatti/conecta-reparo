package com.unnamed.conectareparo.maintenanceaction.controller;

import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.maintenanceaction.service.MaintenanceActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actions")
@Tag(name = "Maintenance Action", description = "Endpoints for managing all maintenance actions")
public class MaintenanceActionAdminController {

    private final MaintenanceActionService maintenanceActionService;

    public MaintenanceActionAdminController(MaintenanceActionService maintenanceActionService) {
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
    @GetMapping
    public ResponseEntity<List<MaintenanceActionResponseDto>> getAllActions() {
        List<MaintenanceActionResponseDto> actionsList = maintenanceActionService.getAllActions();
        return ResponseEntity.ok(actionsList);
    }
}
