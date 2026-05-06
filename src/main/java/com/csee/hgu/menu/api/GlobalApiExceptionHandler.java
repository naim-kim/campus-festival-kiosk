package com.csee.hgu.menu.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handle(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            msg = "Unexpected server error";
        }
        ApiErrorResponse body = new ApiErrorResponse(msg, ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

