package com.sprint.food_delivery.CheckoutModule.Ratings;

import com.sprint.food_delivery.OrderModule.Orders.Orders;
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
@Table(name = "ratings")
public class Ratings {

    @Id
    @Column(name = "rating_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurants restaurant;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "review")
    private String review;


    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Restaurants getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurants restaurant) {
        this.restaurant = restaurant;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}