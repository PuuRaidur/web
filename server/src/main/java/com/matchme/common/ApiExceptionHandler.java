package com.matchme.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    // map "profile incomplete" error to HTTP 403
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        if ("Profile incomplete".equals(ex.getMessage())) {
            return ResponseEntity.status(404).body("Profile incomplete");
        }
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
