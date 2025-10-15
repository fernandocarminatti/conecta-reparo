package com.unnamed.conectareparo.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the category of a maintenance request.
 *
 * <p>Each category identifies the main area or type of maintenance work performed.</p>
 */
public enum MaintenanceCategory {
    @Schema(description = "Building-related maintenance issues, such as structural repairs or renovations.")
    BUILDING,
    @Schema(description = "Electrical system maintenance, including wiring, outlets, and lighting.")
    ELECTRICAL,
    @Schema(description = "Plumbing-related maintenance, such as leaks, pipe repairs, and fixture installations.")
    PLUMBING,
    @Schema(description = "Heating, ventilation, and air conditioning (HVAC) system maintenance.")
    HVAC,
    @Schema(description = "Appliance repairs and maintenance, including refrigerators, ovens, and washing machines.")
    FURNITURE,
    @Schema(description = "Landscaping and outdoor maintenance, such as lawn care and tree trimming.")
    GARDENING,
    @Schema(description = "Security-related maintenance, including locks, alarms, and surveillance systems.")
    SECURITY,
    @Schema(description = "Other maintenance issues that do not fit into the predefined categories.")
    OTHERS;
}