package com.sprint.food_delivery.Exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Integer id) {
        super("Customer not found with id: " + id);
    }
}