package com.sprint.food_delivery.RestaurantsModule.Restaurants;

import java.util.List;

public interface IRestaurantsService {
    RestaurantResponseDTO save(RestaurantsRequestDTO dto);
    List<RestaurantResponseDTO> getAll();
    RestaurantResponseDTO findById(Integer id);
    RestaurantResponseDTO update(Integer id, RestaurantsRequestDTO dto);
    String delete(Integer id);
}