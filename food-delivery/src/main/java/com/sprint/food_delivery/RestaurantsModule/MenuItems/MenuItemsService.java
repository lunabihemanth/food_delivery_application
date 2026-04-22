package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.Restaurants;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRepository;

@Service
public class MenuItemsService implements IMenuItemsService {

    @Autowired
    private MenuItemsRepository repository;

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    // CREATE
    @Override
    public MenuItemsResponseDTO save(MenuItemsRequestDTO dto) {

        // Validation
        if (dto.getItemName() == null || dto.getItemName().isBlank()) {
            throw new BadRequestException("Item name cannot be empty");
        }

        if (dto.getItemPrice() == null || dto.getItemPrice() <= 0) {
            throw new BadRequestException("Item price must be greater than 0");
        }

        // Restaurant must exist
        Restaurants restaurant = restaurantsRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + dto.getRestaurantId()));

        MenuItems item = new MenuItems();
        item.setItemName(dto.getItemName());
        item.setItemDescription(dto.getItemDescription());
        item.setItemPrice(dto.getItemPrice());
        item.setRestaurant(restaurant);

        return map(repository.save(item));
    }

    // GET ALL
    @Override
    public List<MenuItemsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public MenuItemsResponseDTO findById(Integer id) {

        MenuItems item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        return map(item);
    }

    // GET BY RESTAURANT
    @Override
    public List<MenuItemsResponseDTO> getByRestaurantId(Integer restaurantId) {

        // Validate restaurant exists 
        if (!restaurantsRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return repository.findByRestaurant_RestaurantId(restaurantId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public MenuItemsResponseDTO update(Integer id, MenuItemsRequestDTO dto) {

        MenuItems existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        Restaurants restaurant = restaurantsRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + dto.getRestaurantId()));

        //  Validation
        if (dto.getItemName() == null || dto.getItemName().isBlank()) {
            throw new BadRequestException("Item name cannot be empty");
        }

        if (dto.getItemPrice() == null || dto.getItemPrice() <= 0) {
            throw new BadRequestException("Item price must be greater than 0");
        }

        existing.setItemName(dto.getItemName());
        existing.setItemDescription(dto.getItemDescription());
        existing.setItemPrice(dto.getItemPrice());
        existing.setRestaurant(restaurant);

        return map(repository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        MenuItems existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        repository.delete(existing);

        return "Menu item deleted successfully with id: " + id;
    }

    // MAPPER
    private MenuItemsResponseDTO map(MenuItems m) {
        return new MenuItemsResponseDTO(
                m.getItemId(),
                m.getItemName(),
                m.getItemDescription(),
                m.getItemPrice(),
                m.getRestaurant().getRestaurantId()
        );
    }
}