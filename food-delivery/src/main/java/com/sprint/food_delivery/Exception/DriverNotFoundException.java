package com.sprint.food_delivery.Exception;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(Integer id) {
        super("Driver not found with id: " + id);
    }
}