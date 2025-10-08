package com.unnamed.conectareparo.controller;

import com.unnamed.conectareparo.dto.NewPledgeRequestDto;
import com.unnamed.conectareparo.dto.PledgeResponseDto;
import com.unnamed.conectareparo.dto.PledgeUpdateDto;
import com.unnamed.conectareparo.service.PledgeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pledges")
public class PledgeController {

    private final PledgeService pledgeService;

    public PledgeController(PledgeService pledgeService) {
        this.pledgeService = pledgeService;
    }

    @PostMapping
    public ResponseEntity<PledgeResponseDto> createPledge( @Valid @RequestBody NewPledgeRequestDto pledgeRequestDto) {
        PledgeResponseDto pledgeResponseDto = pledgeService.createPledge(pledgeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pledgeResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<PledgeResponseDto>> getPledgesForMaintenanceId(@RequestParam UUID maintenanceId, Pageable pageable) {
        Page<PledgeResponseDto> pledges = pledgeService.getPledgesByMaintenanceId(pageable, maintenanceId);
        return ResponseEntity.ok(pledges);
    }

    @PatchMapping("/{pledgeId}")
    public ResponseEntity<PledgeResponseDto> updatePledgeStatus(
            @PathVariable UUID pledgeId,
            @Valid @RequestBody PledgeUpdateDto pledgeUpdateDto) {
        PledgeResponseDto updatedPledge = pledgeService.updatePledge(pledgeId, pledgeUpdateDto);
        return ResponseEntity.ok(updatedPledge);
    }
}