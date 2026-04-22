package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import java.util.List;

public interface IOrdersCouponsService {
    OrdersCouponsResponseDTO applyCoupon(OrdersCouponsRequestDTO dto);
    List<OrdersCouponsResponseDTO> getCouponsByOrderId(Integer orderId);
    String removeCoupon(Integer orderId, Integer couponId); //* */
}