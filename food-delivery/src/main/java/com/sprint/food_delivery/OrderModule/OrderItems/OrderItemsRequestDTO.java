package com.sprint.food_delivery.OrderModule.OrderItems;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemsRequestDTO {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be atleast 1")
    private Integer quantity;

    @NotNull(message = "Order ID is required")
    private Integer orderId;

    @NotNull(message = "Menu Item ID is required")
    private Integer itemId;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}