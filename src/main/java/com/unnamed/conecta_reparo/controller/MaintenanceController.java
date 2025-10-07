package com.unnamed.conecta_reparo.controller;

import com.unnamed.conecta_reparo.dto.MaintenanceResponseDto;
import com.unnamed.conecta_reparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conecta_reparo.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
}