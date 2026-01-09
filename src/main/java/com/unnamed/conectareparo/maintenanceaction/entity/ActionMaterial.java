package com.unnamed.conectareparo.maintenanceaction.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing a material used during a maintenance action.
 * It includes details such as the item name, quantity, and unit of measure.
 */
@Entity
@Table(name = "action_material")
public class ActionMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id")
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_action_id")
    private MaintenanceAction maintenanceAction;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    protected ActionMaterial() {
    }

    public ActionMaterial(String itemName, BigDecimal quantity, String unitOfMeasure) {
        this.publicId = UUID.randomUUID();
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public MaintenanceAction getMaintenanceAction() {
        return maintenanceAction;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateDetails(String itemName, BigDecimal quantity, String unitOfMeasure) {
        if (itemName != null){this.itemName = itemName;}
        if (quantity != null){this.quantity = quantity;}
        if (unitOfMeasure != null){this.unitOfMeasure = unitOfMeasure;}
    }

    protected void setMaintenanceAction(MaintenanceAction maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

    @PreUpdate
    private void onPreUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

}