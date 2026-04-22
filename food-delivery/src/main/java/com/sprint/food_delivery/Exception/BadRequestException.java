package com.sprint.food_delivery.Exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message );
    }
}