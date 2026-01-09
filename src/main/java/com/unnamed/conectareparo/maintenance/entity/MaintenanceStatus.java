package com.unnamed.conectareparo.maintenance.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the current status of a maintenance request.
 *
 * <p>Possible values:</p>
 * <ul>
 *   <li><b>OPEN</b> – Request created but not yet started.</li>
 *   <li><b>IN_PROGRESS</b> – Maintenance is currently being performed.</li>
 *   <li><b>COMPLETED</b> – Maintenance has been finished successfully.</li>
 *   <li><b>CANCELED</b> – Request canceled and will not be executed.</li>
 * </ul>
 */
@Schema(description = "Enumeration representing the status of a maintenance request")
public enum MaintenanceStatus {
    @Schema(description = "Request created but not yet started.")
    OPEN,
    @Schema(description = "Maintenance is currently being performed.")
    IN_PROGRESS,
    @Schema(description = "Maintenance has been finished successfully.")
    COMPLETED,
    @Schema(description = "Request canceled and will not be executed.")
    CANCELED
}