package com.unnamed.conectareparo.repository;

import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceActionRepository extends JpaRepository<MaintenanceAction, Long> {
    /**
     * Finds all MaintenanceActions for a given Maintenance entity.
     * It uses a JOIN FETCH to eagerly load the associated materialsUsed collection,
     * solving the N+1 query problem.
     * The query is highly efficient as it uses the primary key of the Maintenance entity.
     *
     * @param maintenance The parent Maintenance entity.
     * @return A List of MaintenanceAction entities with their materials initialized.
     */
    @Query("SELECT ma FROM MaintenanceAction ma LEFT JOIN FETCH ma.materialsUsed WHERE ma.maintenance = :maintenance ORDER BY ma.createdAt DESC")
    List<MaintenanceAction> findAllByMaintenanceWithMaterials(@Param("maintenance") Maintenance maintenance);

    /**
     * Finds a specific MaintenanceAction by its id within the context of a given Maintenance entity.
     * @param maintenance The parent Maintenance entity.
     * @param actionPublicId The public facing id of the MaintenanceAction to find.
     * @return An Optional containing the found MaintenanceAction with its materials initialized, or empty if not found.
     */
    @Query("SELECT ma FROM MaintenanceAction ma LEFT JOIN FETCH ma.materialsUsed WHERE ma.maintenance = :maintenance AND ma.publicId = :actionPublicId")
    Optional<MaintenanceAction> findByMaintenanceAndActionPublicId(
            @Param("maintenance") Maintenance maintenance,
            @Param("actionPublicId") UUID actionPublicId);

}