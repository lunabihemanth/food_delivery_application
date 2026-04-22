package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu-items")
public class MenuItemsController {

    @Autowired
    private IMenuItemsService service;

    private Map<String, Object> build(int s, String m, Object d) {
        Map<String, Object> r = new HashMap<>();
        r.put("status", s);
        r.put("message", m);
        r.put("data", d);
        r.put("timestamp", LocalDateTime.now());
        return r;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MenuItemsRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(build(201, "Item created", service.save(dto)));
    }

        @PostMapping("/restaurants/{restaurantId}/menu-items")
            public ResponseEntity<?> createForRestaurant(
            @PathVariable Integer restaurantId,
            @RequestBody MenuItemsRequestDTO dto) {

        dto.setRestaurantId(restaurantId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(build(201, "Item created", service.save(dto)));
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<?> getByRestaurantNested(@PathVariable Integer restaurantId) {

        return ResponseEntity.ok(
                build(200,
                    "Items fetched",
                    service.getByRestaurantId(restaurantId))
        );
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(build(200, "Items fetched", service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(build(200, "Item fetched", service.findById(id)));
    }

    @GetMapping("/menu-items/{restaurantId}")
    public ResponseEntity<?> getByRestaurant(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(build(200, "Items fetched",
                service.getByRestaurantId(restaurantId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                   @Valid @RequestBody MenuItemsRequestDTO dto) {
        return ResponseEntity.ok(build(200, "Item updated", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(build(200, "Item deleted", null));
    }
}