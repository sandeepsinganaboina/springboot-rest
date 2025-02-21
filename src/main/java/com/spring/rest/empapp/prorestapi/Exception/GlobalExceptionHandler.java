package com.spring.rest.empapp.prorestapi.Exception;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
}
