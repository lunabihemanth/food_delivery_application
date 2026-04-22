package com.sprint.food_delivery.CheckoutModule.Ratings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RatingController {

    @Autowired
    private IRatingsService ratingsService;

    
    @PostMapping("/orders/{orderId}/ratings")
    public RatingsResponseDTO addRating(
            @PathVariable Integer orderId,
            @RequestBody RatingsRequestDTO dto) {

        dto.setOrderId(orderId); 
        return ratingsService.save(dto);
    }


    @GetMapping("/restaurants/{restaurantId}/ratings")
    public List<RatingsResponseDTO> getRatingsByRestaurant(
            @PathVariable Integer restaurantId) {

        return ratingsService.getByRestaurantId(restaurantId);
    }


    @GetMapping("/ratings/{ratingId}")
    public RatingsResponseDTO getRatingById(
            @PathVariable Integer ratingId) {

        return ratingsService.findById(ratingId);
    }


    @DeleteMapping("/ratings/{ratingId}")
    public String deleteRating(
            @PathVariable Integer ratingId) {

        return ratingsService.delete(ratingId);
    }
}