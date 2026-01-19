package com.example.todo.dto;

public class FieldErrorDetail {
    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    // Getters
    public String getField() { return field; }
    public String getMessage() { return message; }
}
