package com.unnamed.conectareparo.repository;

import com.unnamed.conectareparo.entity.MaintenanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceActionRepository extends JpaRepository<MaintenanceAction, Long> {
}