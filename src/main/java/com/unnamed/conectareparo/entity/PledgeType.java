package com.unnamed.conectareparo.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the type of pledge.
 * <p>Possible values:</p>
 * <ul>
 *   <li><b>MATERIAL</b> – Pledge for material resources or items.</li>
 *   <li><b>LABOR</b> – Pledge for labor or services.</li>
 * </ul>
 */
public enum PledgeType {
    @Schema(description = "Pledge for material resources or items.")
    MATERIAL,
    @Schema(description = "Pledge for labor or services.")
    LABOR,
}