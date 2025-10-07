package com.unnamed.conectareparo.repository;

import com.unnamed.conectareparo.entity.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    Optional<Maintenance> findByPublicId(UUID uuid);
    Page<Maintenance> findAll(Pageable pageable);
}