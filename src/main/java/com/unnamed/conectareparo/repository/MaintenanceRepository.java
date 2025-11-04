package com.unnamed.conectareparo.repository;

import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long>, JpaSpecificationExecutor<Maintenance> {
    Optional<Maintenance> findByPublicId(UUID uuid);
    Page<Maintenance> findAll(Specification spec, Pageable pageable);
    Page<Maintenance> findByStatusIn(List<MaintenanceStatus> status, Pageable pageable);
}