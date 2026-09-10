package com.example.library.api.dto;

public class ErrorResponseDto {

    private final String error;

    public ErrorResponseDto(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
