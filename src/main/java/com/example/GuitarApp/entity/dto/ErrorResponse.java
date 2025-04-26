package com.example.GuitarApp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse{
    private int status;
    private String error;
    private String message;
    private String stackTrace;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, String stackTrace) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.stackTrace = stackTrace;
        this.timestamp = LocalDateTime.now();
    }
}
