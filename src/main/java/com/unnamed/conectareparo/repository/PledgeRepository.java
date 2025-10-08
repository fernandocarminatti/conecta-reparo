package com.unnamed.conectareparo.repository;

import com.unnamed.conectareparo.entity.Pledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PledgeRepository extends JpaRepository<Pledge, Long> {
    Page<Pledge> findByMaintenanceId(UUID maintenanceId, Pageable pageable);
}