package com.unnamed.conectareparo.pledge.service;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.pledge.dto.PledgeDto;
import com.unnamed.conectareparo.pledge.dto.PledgeResponseDto;
import com.unnamed.conectareparo.pledge.dto.PledgeUpdateDto;
import com.unnamed.conectareparo.pledge.entity.Pledge;
import com.unnamed.conectareparo.pledge.mapper.PledgeMapper;
import com.unnamed.conectareparo.pledge.repository.PledgeRepository;
import com.unnamed.conectareparo.common.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.maintenance.service.MaintenanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for managing community pledges (offers of help).
 * This service handles the business logic for creating, retrieving, and updating pledges,
 * including enforcing rules that depend on the state of the parent Maintenance task.
 */
@Service
@Transactional(readOnly = true)
public class PledgeService {

    private final PledgeRepository pledgeRepository;
    private final PledgeMapper pledgeMapper;
    private final MaintenanceService maintenanceService;

    public PledgeService(PledgeRepository pledgeRepository, PledgeMapper pledgeMapper, MaintenanceService maintenanceService) {
        this.pledgeRepository = pledgeRepository;
        this.pledgeMapper = pledgeMapper;
        this.maintenanceService = maintenanceService;
    }

    /**
     * Creates a new pledge for a specific maintenance task.
     * It enforces the business rule that pledges cannot be added to a maintenance task
     * that is already in a terminal state (COMPLETED or CANCELLED).
     *
     * @param pledgeRequestDto The DTO containing the data for the new pledge.
     * @return A DTO representing the newly created pledge.
     * @throws ResourceNotFoundException if the parent Maintenance task is not found.
     * @throws IllegalStateException if the parent Maintenance task is in a terminal state.
     */
    @Transactional
    public PledgeResponseDto createPledge(PledgeDto pledgeRequestDto) {
        Maintenance foundMaintenance = maintenanceService.getMaintenanceEntityByPublicId(pledgeRequestDto.maintenanceId());
        if (foundMaintenance.getStatus() == MaintenanceStatus.COMPLETED || foundMaintenance.getStatus() == MaintenanceStatus.CANCELED) {
            throw new IllegalStateException("Cannot create a pledge for a maintenance that is in a terminal state.");
        }
        Pledge pledge = pledgeMapper.toEntity(foundMaintenance, pledgeRequestDto);
        pledgeRepository.save(pledge);
        return pledgeMapper.toResponseDto(pledge);
    }

    /**
     * Retrieves a paginated list of all pledges associated with a specific maintenance task.
     *
     * @param pageable The pagination information (page, size, sort).
     * @param maintenanceId The public UUID of the parent Maintenance task.
     * @return A {@link Page} of DTOs representing the pledges for the given maintenance task.
     */
    public Page<PledgeResponseDto> getPledgesByMaintenanceId(Pageable pageable, UUID maintenanceId) {
        Page<Pledge> pledges = pledgeRepository.findAllByMaintenancePublicId(maintenanceId, pageable);
        return pledges.map(pledgeMapper::toResponseDto);
    }

    /**
     * Updates an existing pledge with the provided data.
     * The update logic is delegated to the rich domain model of the Pledge entity.
     *
     * @param pledgeId The public UUID of the pledge to update.
     * @param pledgeUpdateDto The DTO containing the fields to be updated.
     * @return A DTO representing the updated state of the pledge.
     * @throws ResourceNotFoundException if no pledge with the given public ID is found.
     * @throws IllegalStateException if the update violates a business rule within the entity (e.g., invalid status transition).
     */
    @Transactional
    public PledgeResponseDto updatePledge(UUID pledgeId, PledgeUpdateDto pledgeUpdateDto) {
        Pledge existingPledge = pledgeRepository.findByPublicId(pledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Pledge not found with id: " + pledgeId));
        existingPledge.updateDetails(
                pledgeUpdateDto.volunteerName(),
                pledgeUpdateDto.volunteerContact(),
                pledgeUpdateDto.description(),
                pledgeUpdateDto.type()
        );
        existingPledge.updateStatus(pledgeUpdateDto.status());
        pledgeRepository.save(existingPledge);
        return pledgeMapper.toResponseDto(existingPledge);
    }
}