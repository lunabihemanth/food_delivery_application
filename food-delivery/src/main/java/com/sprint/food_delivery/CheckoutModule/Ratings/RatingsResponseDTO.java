package com.sprint.food_delivery.CheckoutModule.Ratings;

public class RatingsResponseDTO {

    private Integer ratingId;
    private Integer orderId; //which order this rating belongs to
    private Integer restaurantId; //which restaurant was rated
    private Integer rating;
    private String review;

    public RatingsResponseDTO(Integer ratingId,
                              Integer orderId,
                              Integer restaurantId,
                              Integer rating,
                              String review) {
        this.ratingId = ratingId;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.review = review;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
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