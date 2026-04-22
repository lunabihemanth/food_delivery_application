package com.sprint.food_delivery.CheckoutModule.Ratings;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.Orders;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRepository;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.Restaurants;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRepository;

import jakarta.transaction.Transactional;

@Service
public class RatingsService implements IRatingsService {

    @Autowired
    private RatingsRepository ratingsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    // Create
    @Override
    public RatingsResponseDTO save(RatingsRequestDTO dto) {

        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        Restaurants restaurant = restaurantsRepository.findById(dto.getRestaurantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found with id: " + dto.getRestaurantId()));

        // order must belong to same restaurant
        if (!order.getRestaurant().getRestaurantId().equals(dto.getRestaurantId())) {
            throw new BadRequestException("Order does not belong to this restaurant");
        }

        // only after delivery
        if (!"DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new BadRequestException("You can rate only after order is delivered");
        }

        // only one rating per order
        if (ratingsRepository.existsByOrder_OrderId(dto.getOrderId())) {
            throw new ConflictException("Rating already exists for this order");
        }

        // rating range
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Ratings rating = new Ratings();
        rating.setOrder(order);
        rating.setRestaurant(restaurant);
        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());

        return map(ratingsRepository.save(rating));
    }

    // Get All
    @Override
    public List<RatingsResponseDTO> getAll() {
        return ratingsRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public RatingsResponseDTO findById(Integer ratingId) {

        Ratings rating = ratingsRepository.findById(ratingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Rating not found with id: " + ratingId));

        return map(rating);
    }

    //  GET BY RESTAURANT
    @Override
    public List<RatingsResponseDTO> getByRestaurantId(Integer restaurantId) {

        if (!restaurantsRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return ratingsRepository.findByRestaurantRestaurantId(restaurantId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // HIGH RATINGS
    @Override
    public List<RatingsResponseDTO> getHighRatings(Integer restaurantId, double rating) {

        return ratingsRepository.findHighRatingsByRestaurant(restaurantId, rating)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // AVERAGE RATING
    @Override
    public Double getAverageRating(Integer restaurantId) {

        if (!restaurantsRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return ratingsRepository.getAverageRatingByRestaurant(restaurantId);
    }

    // UPDATE
    @Override
    public RatingsResponseDTO update(Integer ratingId, RatingsRequestDTO dto) {

        Ratings existing = ratingsRepository.findById(ratingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Rating not found with id: " + ratingId));

        // Validate rating
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        existing.setRating(dto.getRating());
        existing.setReview(dto.getReview());

        return map(ratingsRepository.save(existing));
    }

    // UPDATE (PARTIAL)
    @Override
    @Transactional
    public String updateRatingValue(Integer ratingId, double rating) {

        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        int updated = ratingsRepository.updateRatingValue(ratingId, rating);

        if (updated == 0) {
            throw new ResourceNotFoundException("Rating not found with id: " + ratingId);
        }

        return "Rating updated successfully";
    }

    // DELETE
    @Override
    public String delete(Integer ratingId) {

        if (!ratingsRepository.existsById(ratingId)) {
            throw new ResourceNotFoundException("Rating not found with id: " + ratingId);
        }

        ratingsRepository.deleteById(ratingId);

        return "Rating deleted successfully";
    }

    // MAPPER
    private RatingsResponseDTO map(Ratings rating) {
        return new RatingsResponseDTO(
                rating.getRatingId(),
                rating.getOrder().getOrderId(),
                rating.getRestaurant().getRestaurantId(),
                rating.getRating(),
                rating.getReview()
        );
    }
}