package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.NewPledgeRequestDto;
import com.unnamed.conectareparo.dto.PledgeResponseDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.Pledge;
import com.unnamed.conectareparo.mapper.PledgeMapper;
import com.unnamed.conectareparo.repository.PledgeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PledgeService {

    private final PledgeRepository pledgeRepository;
    private final PledgeMapper pledgeMapper;
    private final MaintenanceService maintenanceService;

    public PledgeService(PledgeRepository pledgeRepository, PledgeMapper pledgeMapper, MaintenanceService maintenanceService) {
        this.pledgeRepository = pledgeRepository;
        this.pledgeMapper = pledgeMapper;
        this.maintenanceService = maintenanceService;
    }

    public PledgeResponseDto createPledge(NewPledgeRequestDto pledgeRequestDto) {
        Maintenance foundMaintenance = maintenanceService.getMaintenanceEntityByPublicId(pledgeRequestDto.maintenanceId());
        Pledge pledge = pledgeMapper.toEntity(foundMaintenance, pledgeRequestDto);
        pledgeRepository.save(pledge);
        return pledgeMapper.toResponseDto(pledge);
    }

    public Page<PledgeResponseDto> getPledgesByMaintenanceId(Pageable pageable, UUID maintenanceId) {
        Page<Pledge> pledges = pledgeRepository.findAllByMaintenancePublicId(maintenanceId, pageable);
        return pledges.map(pledgeMapper::toResponseDto);
    }
}