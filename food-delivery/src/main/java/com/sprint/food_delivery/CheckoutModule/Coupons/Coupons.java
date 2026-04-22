package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupons {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer couponId;

    @Column(name = "coupon_code", unique = true)
    private String couponCode;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}