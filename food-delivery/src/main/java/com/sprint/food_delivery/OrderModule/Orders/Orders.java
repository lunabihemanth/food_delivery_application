package com.sprint.food_delivery.OrderModule.Orders;

import java.time.LocalDateTime;

import com.sprint.food_delivery.CustomersModule.Customers.Customers;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDrivers;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.Restaurants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurants restaurant;

    @ManyToOne
    @JoinColumn(name = "delivery_driver_id")
    private DeliveryDrivers deliveryDriver;

    @Column(name = "order_status")
    private String orderStatus;

    // Getters and Setters

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Restaurants getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurants restaurant) {
        this.restaurant = restaurant;
    }

    public DeliveryDrivers getDeliveryDriver() {
        return deliveryDriver;
    }

    public void setDeliveryDriver(DeliveryDrivers deliveryDriver) {
        this.deliveryDriver = deliveryDriver;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}