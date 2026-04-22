package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

public class OrdersCouponsResponseDTO {

    private Integer orderId;
    private Integer couponId;

    public OrdersCouponsResponseDTO(Integer orderId, Integer couponId) {
        this.orderId = orderId;
        this.couponId = couponId;
    }

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