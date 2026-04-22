package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrdersCouponsRepository extends JpaRepository<OrdersCoupons, OrdersCouponsId> {

 
    // Get all coupons applied to an order
    List<OrdersCoupons> findByOrder_OrderId(Integer orderId);


    // Fetch all mappings for a given coupon
    @Query("SELECT oc FROM OrdersCoupons oc WHERE oc.coupon.couponId = :couponId")
    List<OrdersCoupons> findByCouponId(@Param("couponId") Integer couponId);


    @Modifying
    @Transactional
    @Query("DELETE FROM OrdersCoupons oc WHERE oc.order.orderId = :orderId AND oc.coupon.couponId = :couponId")
    int deleteByOrderAndCoupon(@Param("orderId") Integer orderId,
                               @Param("couponId") Integer couponId);
}