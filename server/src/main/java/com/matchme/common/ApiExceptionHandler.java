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
            return ResponseEntity.status(403).body("Profile incomplete");
        }
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // map validation errors (e.g., email already in use) to HTTP 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
