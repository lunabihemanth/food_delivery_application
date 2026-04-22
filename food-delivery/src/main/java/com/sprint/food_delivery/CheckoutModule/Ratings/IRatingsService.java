package com.sprint.food_delivery.CheckoutModule.Ratings;

import java.util.List;



public interface IRatingsService {
    RatingsResponseDTO save(RatingsRequestDTO dto);
    List<RatingsResponseDTO> getAll();
    RatingsResponseDTO findById(Integer ratingId);
    List<RatingsResponseDTO> getByRestaurantId(Integer restaurantId);  
    List<RatingsResponseDTO> getHighRatings(Integer restaurantId, double rating);
    Double getAverageRating(Integer restaurantId);
    RatingsResponseDTO update(Integer ratingId, RatingsRequestDTO dto);
    String updateRatingValue(Integer ratingId, double rating);
    String delete(Integer ratingId);
}
