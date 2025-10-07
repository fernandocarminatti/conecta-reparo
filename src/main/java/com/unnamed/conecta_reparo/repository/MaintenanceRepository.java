package com.unnamed.conecta_reparo.repository;

import com.unnamed.conecta_reparo.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    Optional<Maintenance> findByPublicId(UUID uuid);
}