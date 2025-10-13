package com.unnamed.conectareparo.entity;

/**
 * Enum representing the possible outcomes of a maintenance action.
 * - SUCCESS: The action was completed successfully without any issues.
 * - PARTIAL_SUCCESS: The action was completed but with some issues or incomplete aspects.
 * - FAILURE: The action was not completed successfully and encountered significant issues.
 */
public enum ActionOutcomeStatus {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILURE
}