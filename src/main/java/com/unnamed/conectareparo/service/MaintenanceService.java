package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceMapper;
import com.unnamed.conectareparo.repository.MaintenanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer responsible for business logic related to Maintenance tasks.
 * It handles the creation, retrieval, and modification of Maintenance entities,
 * orchestrating interactions with the repository and mapper layers.
 */
@Service
@Transactional(readOnly = true)
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceMapper maintenanceMapper;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, MaintenanceMapper maintenanceMapper) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceMapper = maintenanceMapper;
    }

    /**
     * Creates a new Maintenance task based on the provided data.
     * This operation is performed in a writable transaction.
     *
     * @param maintenanceDTO The DTO containing the data for the new maintenance task.
     * @return A DTO representing the newly created maintenance task.
     */
    @Transactional
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

    /**
     * Retrieves a paginated list of all Maintenance tasks.
     * This operation is performed in a read-only transaction for performance.
     *
     * @param pageable The pagination information (page number, size, and sorting).
     * @return A {@link Page} of DTOs representing the maintenance tasks.
     */
    public Page<MaintenanceResponseDto> getAllMaintenances(Pageable pageable) {
        Page<Maintenance> pageOfMaintenance = maintenanceRepository.findAll(pageable);
        return pageOfMaintenance.map(maintenanceMapper::toResponseDto);
    }

    /**
     * Retrieves a single Maintenance task by its public ID.
     *
     * @param publicId The public UUID of the maintenance task to retrieve.
     * @return A DTO representing the requested maintenance task.
     * @throws ResourceNotFoundException if no maintenance task with the given public ID is found.
     */
    public MaintenanceResponseDto getMaintenanceByPublicId(UUID publicId) {
        Maintenance maintenance = maintenanceRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        return maintenanceMapper.toResponseDto(maintenance);
    }

    /**
     * Updates an existing Maintenance task with the provided data.
     * This method delegates the update logic to the rich domain model of the Maintenance entity.
     * This operation is performed in a writable transaction.
     *
     * @param publicId The public UUID of the maintenance task to update.
     * @param updateDto The DTO containing the fields to be updated.
     * @return A DTO representing the updated state of the maintenance task.
     * @throws ResourceNotFoundException if no maintenance task with the given public ID is found.
     * @throws IllegalStateException if the update violates a business rule within the entity (e.g., invalid status transition).
     */
    @Transactional
    public MaintenanceResponseDto updateMaintenance(UUID publicId, MaintenanceUpdateDto updateDto){
        Maintenance maintenance = maintenanceRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.updateDetails(
                updateDto.title(),
                updateDto.description(),
                updateDto.category());
        maintenance.changeStatus(updateDto.status());
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return maintenanceMapper.toResponseDto(updatedMaintenance);
    }

    public Page<MaintenanceResponseDto> getActiveMaintenances(){
        List<MaintenanceStatus> statusFilters = List.of(
                MaintenanceStatus.OPEN,
                MaintenanceStatus.IN_PROGRESS
        );
        Page<Maintenance> pageOfMaintenance = maintenanceRepository.findByStatusIn(
                statusFilters,
                Pageable.unpaged());
        return pageOfMaintenance.map(maintenanceMapper::toResponseDto);
    }

    /**
     * Retrieves the raw {@link Maintenance} entity by its public ID.
     * This method is intended for internal use by other services within the same package
     * that need to work with the entity itself (e.g., for establishing relationships).
     *
     * @param publicId The public UUID of the maintenance entity to retrieve.
     * @return The raw {@link Maintenance} entity.
     * @throws ResourceNotFoundException if no maintenance task with the given public ID is found.
     */
    protected Maintenance getMaintenanceEntityByPublicId(UUID publicId) {
        return maintenanceRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
    }
}