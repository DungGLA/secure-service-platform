package com.example.secure_service_platform.common.exception;

import com.example.secure_service_platform.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .code("EMAIL_ALREADY_EXISTS")
                        .message(exception.getMessage())
                        .timestamp(java.time.Instant.now())
                        .path("") // You can set the request path here if needed
                        .build());
    }
}