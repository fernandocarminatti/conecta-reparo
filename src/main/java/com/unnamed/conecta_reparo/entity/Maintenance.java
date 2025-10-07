package com.unnamed.conecta_reparo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
    private LocalDateTime scheduledDate;
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    Maintenance() {
    }

    public Maintenance(String title, String description, MaintenanceCategory category, LocalDateTime scheduledDate) {
        this.publicId = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.scheduledDate = scheduledDate;
        this.status = MaintenanceStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public MaintenanceStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(MaintenanceCategory category) {
        this.category = category;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void updateStatus(MaintenanceStatus statusUpdate) {
        this.status = statusUpdate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(id, that.id) && Objects.equals(publicId, that.publicId) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicId, createdAt);
    }
}