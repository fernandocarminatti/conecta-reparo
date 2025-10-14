package com.unnamed.conectareparo.exception;

public class MaintenanceAlreadyCompletedException extends RuntimeException{
    public MaintenanceAlreadyCompletedException(String message) {
        super(message);
    }
}