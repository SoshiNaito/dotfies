package com.example.todo.dto;

import java.util.List;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private List<FieldErrorDetail> details;

    public ErrorResponse(int status, String error, String message, List<FieldErrorDetail> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<FieldErrorDetail> getDetails() { return details; }
}
