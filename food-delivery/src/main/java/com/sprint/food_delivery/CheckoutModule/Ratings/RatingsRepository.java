package com.sprint.food_delivery.CheckoutModule.Ratings;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RatingsRepository extends JpaRepository<Ratings, Integer> {

    // GET by restaurant
    List<Ratings> findByRestaurantRestaurantId(Integer restaurantId);

    // CHECK if rating already exists for order
    boolean existsByOrder_OrderId(Integer orderId);

    // CUSTOM: HIGH RATINGS FILTER
    @Query("SELECT r FROM Ratings r WHERE r.restaurant.restaurantId = :restaurantId AND r.rating >= :rating")
    List<Ratings> findHighRatingsByRestaurant(
            @Param("restaurantId") Integer restaurantId,
            @Param("rating") double rating
    );

    // CUSTOM: AVERAGE RATING
    @Query("SELECT AVG(r.rating) FROM Ratings r WHERE r.restaurant.restaurantId = :restaurantId")
    Double getAverageRatingByRestaurant(@Param("restaurantId") Integer restaurantId);

    // Partial update
    @Modifying
    @Transactional
    @Query("UPDATE Ratings r SET r.rating = :rating WHERE r.ratingId = :ratingId")
    int updateRatingValue(@Param("ratingId") Integer ratingId,
                          @Param("rating") double rating);
}