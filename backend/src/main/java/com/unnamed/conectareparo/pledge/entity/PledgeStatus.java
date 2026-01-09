package com.unnamed.conectareparo.pledge.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeration representing the various statuses a pledge can have in the system.
 * <p>Possible values:</p>
 * <ul>
 *   <li><b>OFFERED</b> – Offered but not yet reviewed.</li>
 *   <li><b>PENDING</b> - Under review or waiting for any action.</li>
 *   <li><b>REJECTED</b> – The pledge has been Rejected.</li>
 *   <li><b>COMPLETED</b> – The pledge has been Completed.</li>
 *   <li><b>CANCELED</b> – The pledge has been canceled.</li>
 * </ul>
 */

@Schema(description = "Enumeration representing the various statuses a pledge can have in the system")
public enum PledgeStatus {
    @Schema(description = "The pledge has been offered by a volunteer but not yet reviewed.")
    OFFERED,
    @Schema(description = "The pledge is currently under review or waiting for action.")
    PENDING,
    @Schema(description = "The pledge has been rejected.")
    REJECTED,
    @Schema(description = "The pledge has been completed.")
    COMPLETED,
    @Schema(description = "The pledge has been canceled.")
    CANCELED
}