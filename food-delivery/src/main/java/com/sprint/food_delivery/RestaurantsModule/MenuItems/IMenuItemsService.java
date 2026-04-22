package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import java.util.List;

public interface IMenuItemsService {
    MenuItemsResponseDTO save(MenuItemsRequestDTO dto);
    List<MenuItemsResponseDTO> getAll();
    MenuItemsResponseDTO findById(Integer id);
    List<MenuItemsResponseDTO> getByRestaurantId(Integer restaurantId);
    MenuItemsResponseDTO update(Integer id, MenuItemsRequestDTO dto);
    String delete(Integer id);
}