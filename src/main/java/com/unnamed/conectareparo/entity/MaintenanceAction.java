package com.unnamed.conectareparo.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "maintenance_action")
public class MaintenanceAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id")
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id")
    private Maintenance maintenance;

    @OneToMany(
            mappedBy = "maintenanceAction",
            cascade = CascadeType.ALL
    )
    private List<ActionMaterial> materialsUsed = new ArrayList<>();

    private String executedBy;
    private ZonedDateTime startDate;
    private ZonedDateTime completionDate;
    private String actionDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome_status")
    private ActionOutcomeStatus outcomeStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    protected MaintenanceAction() {
    }

    public MaintenanceAction(Maintenance maintenance, String executedBy, ZonedDateTime startDate, ZonedDateTime completionDate, String actionDescription, ActionOutcomeStatus outcomeStatus) {
        this.publicId = UUID.randomUUID();
        this.maintenance = maintenance;
        this.executedBy = executedBy;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.actionDescription = actionDescription;
        this.outcomeStatus = outcomeStatus;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public List<ActionMaterial> getMaterialsUsed() {
        return materialsUsed;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getCompletionDate() {
        return completionDate;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public ActionOutcomeStatus getOutcomeStatus() {
        return outcomeStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateDetails(String executedBy, ZonedDateTime startDate, ZonedDateTime completionDate, String actionDescription, ActionOutcomeStatus outcomeStatus) {
        if(executedBy != null){
            this.executedBy = executedBy;
        }
        if(startDate != null){
            this.startDate = startDate;
        }
        if (completionDate != null && !completionDate.isBefore(this.startDate)) {
            this.completionDate = completionDate;
        }
        if (actionDescription != null){
            this.actionDescription = actionDescription;
        }
        if (outcomeStatus != null){
            this.outcomeStatus = outcomeStatus;
        }
    }

    public void addMaterial(ActionMaterial material) {
        this.materialsUsed.add(material);
        material.setMaintenanceAction(this);
    }

    @PreUpdate
    private void onPreUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}