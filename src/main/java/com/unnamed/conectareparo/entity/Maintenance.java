package com.unnamed.conectareparo.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a maintenance task within the system.
 * Each maintenance task has a unique public identifier, title, description,
 * category, scheduled date, status, and timestamps for creation and last update.
 * The status of the maintenance can be changed, but certain rules apply to prevent
 * invalid state transitions.
 * A maintenance task can be in one of the following states: OPEN, IN_PROGRESS, COMPLETED, CANCELED:
 *  - OPEN: The maintenance task is created and awaiting action.
 *  - IN_PROGRESS: The maintenance task is currently being worked on.
 *  - COMPLETED: The maintenance task has been finished successfully.
 *  - CANCELED: The maintenance task has been canceled and will not be completed.
 *  - Once a maintenance task is marked as COMPLETED or CANCELLED, its status cannot be changed.
 */
@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "public_id")
    private UUID publicId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private MaintenanceCategory category;
    @Column(name = "scheduled_date")
    private ZonedDateTime scheduledDate;
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    public Maintenance() {
    }

    public Maintenance(String title, String description, MaintenanceCategory category, ZonedDateTime scheduledDate) {
        this.publicId = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.scheduledDate = scheduledDate;
        this.status = MaintenanceStatus.OPEN;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public UUID getPublicId() {
        return publicId;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public MaintenanceCategory getCategory() {
        return category;
    }
    public ZonedDateTime getScheduledDate() {
        return scheduledDate;
    }
    public MaintenanceStatus getStatus() {
        return this.status;
    }
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeStatus(MaintenanceStatus statusUpdate) {
        if (statusUpdate == null) {
            return;
        }
        if (this.status == MaintenanceStatus.COMPLETED){
            throw new IllegalStateException("Cannot change status of a completed maintenance.");
        }
        if (this.status == MaintenanceStatus.CANCELED){
            throw new IllegalStateException("Cannot change status of a cancelled maintenance.");
        }
        if (this.status == MaintenanceStatus.IN_PROGRESS && statusUpdate == MaintenanceStatus.OPEN){
            throw new IllegalStateException("Cannot revert status from IN_PROGRESS to OPEN.");
        }
        this.status = statusUpdate;
    }

    public boolean isCompleted(){
        return this.status == MaintenanceStatus.COMPLETED;
    }

    private void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateDetails(String title, String description, MaintenanceCategory category) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (category != null) {
            this.category = category;
        }
    }

    @PreUpdate
    public void onUpdate() {
        setUpdatedAt(ZonedDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}