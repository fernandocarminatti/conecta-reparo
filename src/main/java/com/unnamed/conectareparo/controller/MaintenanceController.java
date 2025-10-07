package com.unnamed.conectareparo.controller;

import com.unnamed.conectareparo.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conectareparo.service.MaintenanceService;
import jakarta.validation.Valid;
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
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping()
    public ResponseEntity<MaintenanceResponseDto> createMaintenance(@Valid @RequestBody NewMaintenanceRequestDto maintenanceDTO) {
        MaintenanceResponseDto createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO);
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMaintenance.id())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(resourceLocation).body(createdMaintenance);
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceResponseDto>> getAllMaintenances(Pageable pageable) {
        Page<MaintenanceResponseDto> foundMaintenances = maintenanceService.getAllMaintenances(pageable);
        return ResponseEntity.ok(foundMaintenances);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<MaintenanceResponseDto> getMaintenanceByPublicId(@PathVariable String publicId) {
        MaintenanceResponseDto foundMaintenance = maintenanceService.getMaintenanceByPublicId(publicId);
        return ResponseEntity.ok(foundMaintenance);
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<MaintenanceResponseDto> updateMaintenance(
            @PathVariable UUID publicId,
            @Valid @RequestBody MaintenanceUpdateDto updateDto){
        MaintenanceResponseDto updatedMaintenance = maintenanceService.updateMaintenance(publicId, updateDto);
        return ResponseEntity.ok(updatedMaintenance);
    }
}