package com.sprint.food_delivery.CustomersModule.DeliveryAddress;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class DeliveryAddressController {

    @Autowired
    private IDeliveryAddressService deliveryAddressService;

    // helper method
    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // ADD ADDRESS FOR CUSTOMER
    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<Map<String, Object>> create(
            @PathVariable Integer customerId,
            @Valid @RequestBody DeliveryAddressRequestDTO dto) {

        // inject customerId into DTO
        dto.setCustomerId(customerId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "Address added successfully",
                        deliveryAddressService.save(dto)));
    }

    // GET ALL ADDRESSES OF A CUSTOMER
    @GetMapping("/customers/{customerId}/addresses")
    public ResponseEntity<Map<String, Object>> getByCustomerId(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                buildResponse(200, "Addresses fetched successfully",
                        deliveryAddressService.getByCustomerId(customerId))
        );
    }

    // GET ADDRESS BY ID
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Integer addressId) {

        return ResponseEntity.ok(
                buildResponse(200, "Address fetched successfully",
                        deliveryAddressService.findById(addressId))
        );
    }

    // UPDATE ADDRESS
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Integer addressId,
            @Valid @RequestBody DeliveryAddressRequestDTO dto) {

        return ResponseEntity.ok(
                buildResponse(200, "Address updated successfully",
                        deliveryAddressService.update(addressId, dto))
        );
    }

    // Delete Address
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Integer addressId) {

        deliveryAddressService.delete(addressId);

        return ResponseEntity.ok(
                buildResponse(200, "Address deleted successfully", null)
        );
    }
}