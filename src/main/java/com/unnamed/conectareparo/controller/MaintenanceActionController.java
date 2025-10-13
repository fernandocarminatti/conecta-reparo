package com.unnamed.conectareparo.controller;

import com.unnamed.conectareparo.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.dto.NewMaintenanceActionDto;
import com.unnamed.conectareparo.dto.UpdateMaintenanceActionDto;
import com.unnamed.conectareparo.service.MaintenanceActionService;
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
public class MaintenanceActionController {

    private final MaintenanceActionService maintenanceActionService;

    public MaintenanceActionController(MaintenanceActionService maintenanceActionService) {
        this.maintenanceActionService = maintenanceActionService;
    }

    @PostMapping
    public ResponseEntity<MaintenanceActionResponseDto> createMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @Valid @RequestBody NewMaintenanceActionDto newMaintenanceActionDto) {
        MaintenanceActionResponseDto responseDto = maintenanceActionService.createMaintenanceAction(maintenancePublicId, newMaintenanceActionDto);
        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/maintenances/{maintenancePublicId}/actions/{actionPublicId}")
                .buildAndExpand(maintenancePublicId, responseDto.id())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(resourceLocation).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceActionResponseDto>> getMaintenanceActions(@PathVariable UUID maintenancePublicId) {
        List<MaintenanceActionResponseDto> actionsList = maintenanceActionService.getMaintenanceActions(maintenancePublicId);
        return ResponseEntity.ok(actionsList);
    }

    @GetMapping("/{actionPublicId}")
    public ResponseEntity<MaintenanceActionResponseDto> getSingleMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @PathVariable UUID actionPublicId) {
        MaintenanceActionResponseDto actionDto = maintenanceActionService.getSingleMaintenanceAction(maintenancePublicId, actionPublicId);
        return ResponseEntity.ok(actionDto);
    }

    @PutMapping("/{actionPublicId}")
    public ResponseEntity<MaintenanceActionResponseDto> updateMaintenanceAction(
            @PathVariable UUID maintenancePublicId,
            @PathVariable UUID actionPublicId,
            @Valid @RequestBody UpdateMaintenanceActionDto updatedActionDto) {
        MaintenanceActionResponseDto updatedDto = maintenanceActionService.updateMaintenanceAction(maintenancePublicId, actionPublicId, updatedActionDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
}