package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CouponRequestDTO {

    private Integer couponId;

    @NotBlank(message = "Coupon code cannot be empty")
    private String couponCode;

    @NotNull(message = "Discount amount cannot be null")
    @Positive(message = "Discount amount must be greater than 0")
    private Double discountAmount;

    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    // Getters and Setters

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