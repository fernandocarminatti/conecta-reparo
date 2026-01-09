package com.unnamed.conectareparo.maintenance.specification;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaintenanceSpecification {

    public static Specification<Maintenance> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.equalsIgnoreCase("all")) {
                return null;
            }

            if (status.equalsIgnoreCase("active")) {
                return root.get("status").in(MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS);
            }

            if (status.equalsIgnoreCase("inactive")) {
                return root.get("status").in(MaintenanceStatus.COMPLETED, MaintenanceStatus.CANCELED);
            }

            // Specific status (OPEN, IN_PROGRESS, COMPLETED, CANCELED)
            try {
                MaintenanceStatus statusEnum = MaintenanceStatus.valueOf(status.toUpperCase());
                return cb.equal(root.get("status"), statusEnum);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Maintenance> searchByTerm(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String searchTerm = "%" + search.toLowerCase().trim() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm),
                    cb.like(cb.lower(root.get("category")), searchTerm)
            );
        };
    }

    public static Specification<Maintenance> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("category")), category.toLowerCase());
        };
    }

    public static Specification<Maintenance> scheduledBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return null;
            }
            return cb.between(root.get("scheduledDate"), startDate, endDate);
        };
    }

    public static Specification<Maintenance> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }
}