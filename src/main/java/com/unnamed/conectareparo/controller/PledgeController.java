package com.unnamed.conectareparo.controller;

import com.unnamed.conectareparo.dto.NewPledgeRequestDto;
import com.unnamed.conectareparo.dto.PledgeResponseDto;
import com.unnamed.conectareparo.service.PledgeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}