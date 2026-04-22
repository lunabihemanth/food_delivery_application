package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import jakarta.validation.constraints.NotNull;

public class OrdersCouponsRequestDTO {

    @NotNull(message = "Order ID cannot be null")
    private Integer orderId;

    @NotNull(message = "Coupon ID cannot be null")
    private Integer couponId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }
}