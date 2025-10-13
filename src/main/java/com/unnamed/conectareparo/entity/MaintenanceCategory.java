package com.unnamed.conectareparo.entity;

/**
 * Enumeration representing categories of maintenance tasks.
 * Each category helps classify the type of maintenance being performed.
 * Categories include:
 * - PREDIAL: Building-related maintenance.
 * - ELETRICA: Electrical system maintenance.
 * - HIDRAULICA: Plumbing and water system maintenance.
 * - CLIMATIZACAO: HVAC (Heating, Ventilation, and Air Conditioning) maintenance.
 * - MOBILIARIO: Furniture and fixtures maintenance.
 * - JARDINAGEM: Gardening and landscaping maintenance.
 * - SEGURANCA: Security system maintenance.
 * - OUTROS: Other types of maintenance that is not covered by the above categories.
 */
public enum MaintenanceCategory {
    PREDIAL,
    ELETRICA,
    HIDRAULICA,
    CLIMATIZACAO,
    MOBILIARIO,
    JARDINAGEM,
    SEGURANCA,
    OUTROS;
}