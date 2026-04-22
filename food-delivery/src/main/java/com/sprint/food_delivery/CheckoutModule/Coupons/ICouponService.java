package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.util.List;

public interface ICouponService {
    CouponResponseDTO save(CouponRequestDTO dto);
    List<CouponResponseDTO> getAll();
    CouponResponseDTO findByCode(String couponCode);
    CouponResponseDTO findById(Integer couponId);
    CouponResponseDTO update(Integer couponId, CouponRequestDTO dto);
    String delete(Integer couponId);
}