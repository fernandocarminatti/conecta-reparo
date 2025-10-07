package com.unnamed.conecta_reparo.controller;

import com.unnamed.conecta_reparo.dto.MaintenanceResponseDto;
import com.unnamed.conecta_reparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conecta_reparo.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
}