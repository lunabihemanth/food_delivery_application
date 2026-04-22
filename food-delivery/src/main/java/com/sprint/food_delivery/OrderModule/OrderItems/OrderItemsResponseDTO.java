package com.sprint.food_delivery.OrderModule.OrderItems;


public class OrderItemsResponseDTO {

    private Integer orderItemId;
    private Integer quantity;
    private Integer orderId;
    private Integer itemId;

    public OrderItemsResponseDTO(Integer orderItemId, Integer quantity,
                                 Integer orderId, Integer itemId) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.orderId = orderId;
        this.itemId = itemId;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

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