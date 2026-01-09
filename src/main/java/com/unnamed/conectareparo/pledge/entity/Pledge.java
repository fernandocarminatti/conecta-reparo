package com.unnamed.conectareparo.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a pledge made by a volunteer for a maintenance event.
 * It includes details about the volunteer, the type of pledge, its status, and timestamps.
 * Each pledge is linked to a specific maintenance record.
 * Pledge status can be PENDING, APPROVED, REJECTED, CANCELED, or COMPLETED.
 * - PENDING: The pledge has been made but not yet reviewed.
 * - APPROVED: The pledge has been reviewed and accepted.
 * - REJECTED: The pledge has been reviewed and declined.
 * - CANCELED: The pledge has been withdrawn by the volunteer or an admin.
 * - COMPLETED: The pledge has been fulfilled.
 */
@Entity
@Table(name = "pledge")
public class Pledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID publicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private Maintenance maintenance;

    @Column(name = "volunteer_name")
    private String volunteerName;
    @Column(name = "volunteer_contact")
    private String volunteerContact;
    private String description;
    @Enumerated(EnumType.STRING)
    private PledgeCategory type;
    @Enumerated(EnumType.STRING)
    private PledgeStatus status;
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    Pledge() {}

    public Pledge(Maintenance maintenance, String volunteerName, String volunteerContact, String description, PledgeCategory type) {
        this.publicId = UUID.randomUUID();
        this.maintenance = maintenance;
        this.volunteerName = volunteerName;
        this.volunteerContact = volunteerContact;
        this.description = description;
        this.type = type;
        this.status = PledgeStatus.OFFERED;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Maintenance getMaintenanceId() {
        return maintenance;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public String getVolunteerContact() {
        return volunteerContact;
    }

    public String getDescription() {
        return description;
    }

    public PledgeCategory getType() {
        return type;
    }

    public PledgeStatus getStatus() {
        return status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateDetails(String volunteerName, String volunteerContact, String description, PledgeCategory type) {
        if (volunteerName != null && !volunteerName.isBlank()) {
            this.volunteerName = volunteerName;

        }
        if (volunteerContact != null && !volunteerContact.isBlank()) {
            this.volunteerContact = volunteerContact;
        }
        if (description != null && !description.isBlank()) {
            this.description = description;
        }
        if (type != null){
            this.type = type;
        }
    }

/**
 * Atualiza o status de um compromisso (pledge), aplicando regras de validação para impedir alterações indevidas em estados finais.
 * As seguintes restrições são aplicadas:
 * - Um compromisso cancelado não pode ter o status alterado.
 * - Um compromisso concluído não pode ter o status alterado.
 * - Um compromisso rejeitado não pode ter o status alterado.
 * - Se o novo status for nulo, nenhuma alteração será feita.
 * @param status novo status a ser aplicado
 * @throws IllegalStateException se o compromisso estiver cancelado, concluído ou rejeitado
 */

    public void updateStatus(PledgeStatus status) {
        if(this.status == PledgeStatus.CANCELED){
            throw new IllegalStateException("Cannot change status of a canceled pledge.");
        }
        if(this.status == PledgeStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status of a completed pledge.");
        }
        if(this.status == PledgeStatus.REJECTED) {
            throw new IllegalStateException("Cannot change status of a rejected pledge.");
        }
        if (status != null) {
            this.status = status;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = ZonedDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pledge pledge = (Pledge) o;
        return Objects.equals(publicId, pledge.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}