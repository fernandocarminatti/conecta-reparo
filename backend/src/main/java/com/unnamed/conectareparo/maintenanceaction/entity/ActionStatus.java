package com.unnamed.conectareparo.maintenanceaction.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the possible outcomes of a maintenance action.
 * <p>Possible values:</p>
 * <ul>
 *   <li><b>SUCCESS</b> – The action was completed successfully.</li>
 *   <li><b>PARTIAL_SUCCESS</b> – The action was partially successful, with some issues.</li>
 *   <li><b>FAILURE</b> – The action failed to complete.</li>
 * </ul>
 */
public enum ActionStatus {
    @Schema(description = "The action was completed successfully.")
    SUCCESS,
    @Schema(description = "The action was partially successful, with some issues.")
    PARTIAL_SUCCESS,
    @Schema(description = "The action failed to complete.")
    FAILURE
}