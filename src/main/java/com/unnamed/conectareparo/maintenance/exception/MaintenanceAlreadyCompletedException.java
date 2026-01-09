package com.unnamed.conectareparo.maintenance.exception;

public class MaintenanceAlreadyCompletedException extends RuntimeException{
    public MaintenanceAlreadyCompletedException(String message) {
        super(message);
    }
}