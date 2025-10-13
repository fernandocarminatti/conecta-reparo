package com.unnamed.conectareparo.entity;

/**
 * Enumeration representing the status of a pledge.
 * Possible statuses include:
 * - OFFERED: The pledge has been made but not yet acted upon.
 * - PENDING: The pledge is awaiting confirmation or action.
 * - REJECTED: The pledge has been declined.
 * - COMPLETED: The pledge has been fulfilled.
 * - CANCELED: The pledge has been withdrawn or canceled.
 */
public enum PledgeStatus {
    OFFERED,
    PENDING,
    REJECTED,
    COMPLETED,
    CANCELED
}