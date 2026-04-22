package com.sprint.food_delivery.RestaurantsModule.Restaurants;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ConflictException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;

@Service
public class RestaurantsService implements IRestaurantsService {

    @Autowired
    private RestaurantsRepository repository;

    // CREATE
    @Override
    public RestaurantResponseDTO save(RestaurantsRequestDTO dto) {

        validate(dto);

        // unique restaurant name
        if (repository.existsByRestaurantName(dto.getRestaurantName())) {
            throw new ConflictException("Restaurant already exists with name: " + dto.getRestaurantName());
        }

        Restaurants r = new Restaurants();
        mapToEntity(r, dto);

        return map(repository.save(r));
    }

    // GET ALL
    @Override
    public List<RestaurantResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public RestaurantResponseDTO findById(Integer id) {

        Restaurants restaurant = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        return map(restaurant);
    }

    // UPDATE
    @Override
    public RestaurantResponseDTO update(Integer id, RestaurantsRequestDTO dto) {

        validate(dto);

        Restaurants existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        // prevent duplicate name
        if (!existing.getRestaurantName().equalsIgnoreCase(dto.getRestaurantName()) &&
                repository.existsByRestaurantName(dto.getRestaurantName())) {
            throw new ConflictException("Restaurant name already exists");
        }

        mapToEntity(existing, dto);

        return map(repository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + id);
        }

        repository.deleteById(id);
        
        return "Restaurant deleted successfully";
    }

    // VALIDATION
    private void validate(RestaurantsRequestDTO dto) {

        if (dto.getRestaurantName() == null || dto.getRestaurantName().isBlank()) {
            throw new BadRequestException("Restaurant name cannot be empty");
        }

        if (dto.getRestaurantAddress() == null || dto.getRestaurantAddress().isBlank()) {
            throw new BadRequestException("Restaurant address cannot be empty");
        }

        if (dto.getRestaurantPhone() == null || dto.getRestaurantPhone().isBlank()) {
            throw new BadRequestException("Restaurant phone cannot be empty");
        }
    }

    // ENTITY MAPPER
    private void mapToEntity(Restaurants r, RestaurantsRequestDTO dto) {
        r.setRestaurantName(dto.getRestaurantName());
        r.setRestaurantAddress(dto.getRestaurantAddress());
        r.setRestaurantPhone(dto.getRestaurantPhone());
    }

    // RESPONSE MAPPER
    private RestaurantResponseDTO map(Restaurants r) {
        return new RestaurantResponseDTO(
                r.getRestaurantId(),
                r.getRestaurantName(),
                r.getRestaurantAddress(),
                r.getRestaurantPhone()
        );
    }
}