package com.sprint.food_delivery.Exception;

public class DeliveryAddressNotFoundException extends RuntimeException {

    public DeliveryAddressNotFoundException(Integer id) {
        super("Delivery Address not found with id: " + id);
    }
}