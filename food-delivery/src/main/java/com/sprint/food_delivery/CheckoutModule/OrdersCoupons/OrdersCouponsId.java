package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrdersCouponsId implements Serializable {

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "coupon_id")
    private Integer couponId;

    public OrdersCouponsId() {}

    public OrdersCouponsId(Integer orderId, Integer couponId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdersCouponsId)) return false;
        OrdersCouponsId that = (OrdersCouponsId) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(couponId, that.couponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, couponId);
    }
}