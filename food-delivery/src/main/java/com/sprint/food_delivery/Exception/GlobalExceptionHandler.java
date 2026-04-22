package com.sprint.food_delivery.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", message);
        map.put("timestamp", LocalDateTime.now());
        return map;
    }

    // 🔴 400 - Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 🔴 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // 🔴 409 - Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.CONFLICT, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    // 🔴 Fallback (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // 🔴 Customer Not Found
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // 🔴 Delivery Address Not Found
    @ExceptionHandler(DeliveryAddressNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAddressNotFound(DeliveryAddressNotFoundException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
}

    // 🔴 Driver Not Found
    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDriverNotFound(DriverNotFoundException ex) {
        return new ResponseEntity<>(
                buildResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

}