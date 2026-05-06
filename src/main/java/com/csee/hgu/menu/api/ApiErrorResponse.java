package com.csee.hgu.menu.api;

public class ApiErrorResponse {
    public String message;
    public String exception;

    public ApiErrorResponse(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }
}

