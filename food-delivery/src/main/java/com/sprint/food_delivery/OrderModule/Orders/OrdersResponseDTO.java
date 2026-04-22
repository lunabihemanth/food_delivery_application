package com.sprint.food_delivery.OrderModule.Orders;

import java.time.LocalDateTime;

public class OrdersResponseDTO {

    private Integer orderId;
    private LocalDateTime orderDate;
    private Integer customerId;
    private Integer restaurantId;
    private Integer deliveryDriverId;
    private String orderStatus;

    public OrdersResponseDTO(Integer orderId, LocalDateTime orderDate,
                             Integer customerId, Integer restaurantId,
                             Integer deliveryDriverId, String orderStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryDriverId = deliveryDriverId;
        this.orderStatus = orderStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getDeliveryDriverId() {
        return deliveryDriverId;
    }

    public void setDeliveryDriverId(Integer deliveryDriverId) {
        this.deliveryDriverId = deliveryDriverId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

 
}