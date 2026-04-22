package com.sprint.food_delivery.CheckoutModule.Coupons;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponsRepository extends JpaRepository<Coupons, Integer> {

    //  DERIVED QUERY (used for validation)
    Optional<Coupons> findByCouponCode(String couponCode);



    //  CUSTOM SELECT QUERY (check active/valid coupon if needed later)
    @Query("SELECT c FROM Coupons c WHERE c.couponCode = :code")
    Optional<Coupons> getCouponByCode(@Param("code") String code);

    

    // check if coupon exists
    boolean existsByCouponCode(String couponCode);
}