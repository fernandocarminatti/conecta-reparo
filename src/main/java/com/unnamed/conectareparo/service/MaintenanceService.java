package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceMapper;
import com.unnamed.conectareparo.repository.MaintenanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceMapper maintenanceMapper;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, MaintenanceMapper maintenanceMapper) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceMapper = maintenanceMapper;
    }

    public MaintenanceResponseDto createMaintenance(NewMaintenanceRequestDto maintenanceDTO) {
        Maintenance maintenance = new Maintenance(
                maintenanceDTO.title(),
                maintenanceDTO.description(),
                maintenanceDTO.category(),
                maintenanceDTO.scheduledDate()
        );
        maintenanceRepository.save(maintenance);
        return maintenanceMapper.toResponseDto(maintenance);
    }

    public Page<MaintenanceResponseDto> getAllMaintenances(Pageable pageable) {
        Page<Maintenance> pageOfMaintenance = maintenanceRepository.findAll(pageable);
        return pageOfMaintenance.map(maintenanceMapper::toResponseDto);
    }

    public MaintenanceResponseDto getMaintenanceByPublicId(String publicId) {
        Maintenance maintenance = maintenanceRepository.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        return maintenanceMapper.toResponseDto(maintenance);
    }

    public MaintenanceResponseDto updateMaintenance(UUID publicId, MaintenanceUpdateDto updateDto){
        Maintenance maintenance = maintenanceRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.updateDetails(
                updateDto.title(),
                updateDto.description(),
                updateDto.category());
        maintenance.changeStatus(updateDto.status());
        maintenanceRepository.save(maintenance);
        return maintenanceMapper.toResponseDto(maintenance);
    }
}