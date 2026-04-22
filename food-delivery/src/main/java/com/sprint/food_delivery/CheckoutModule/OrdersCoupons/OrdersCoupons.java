package com.sprint.food_delivery.CheckoutModule.OrdersCoupons;

import com.sprint.food_delivery.CheckoutModule.Coupons.Coupons;
import com.sprint.food_delivery.OrderModule.Orders.Orders;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders_coupons")
public class OrdersCoupons {

    @EmbeddedId
    private OrdersCouponsId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private Coupons coupon;


    public OrdersCouponsId getId() {
        return id;
    }

    public void setId(OrdersCouponsId id) {
        this.id = id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Coupons getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupons coupon) {
        this.coupon = coupon;
    }
}