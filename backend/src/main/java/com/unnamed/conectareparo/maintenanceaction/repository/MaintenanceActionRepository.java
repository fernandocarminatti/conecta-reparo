package com.unnamed.conectareparo.maintenanceaction.repository;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenanceaction.entity.MaintenanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceActionRepository extends JpaRepository<MaintenanceAction, Long> {
    @Query("SELECT ma FROM MaintenanceAction ma LEFT JOIN FETCH ma.materialsUsed WHERE ma.maintenance = :maintenance ORDER BY ma.createdAt DESC")
    List<MaintenanceAction> findAllByMaintenanceWithMaterials(@Param("maintenance") Maintenance maintenance);

    @Query("SELECT ma FROM MaintenanceAction ma LEFT JOIN FETCH ma.materialsUsed WHERE ma.maintenance = :maintenance AND ma.publicId = :actionPublicId")
    Optional<MaintenanceAction> findByMaintenanceAndActionPublicId(
            @Param("maintenance") Maintenance maintenance,
            @Param("actionPublicId") UUID actionPublicId);

    @Query("SELECT ma FROM MaintenanceAction ma LEFT JOIN FETCH ma.materialsUsed ORDER BY ma.createdAt DESC")
    List<MaintenanceAction> findAllWithMaterials();
}