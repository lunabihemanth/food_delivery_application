package com.sprint.food_delivery.OrderModule.OrderItems;

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
@RequestMapping("/orderitems")
public class OrderItemsController {

    @Autowired
    private IOrderItemsService service;

    
    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

  
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> save(
            @Valid @RequestBody OrderItemsRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "OrderItem created successfully",
                        service.save(dto)));
    }


    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll() {

        return ResponseEntity.ok(
                buildResponse(200, "OrderItems fetched successfully",
                        service.getAll())
        );
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                buildResponse(200, "OrderItem fetched successfully",
                        service.findById(id))
        );
    }

 
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Integer id,
            @Valid @RequestBody OrderItemsRequestDTO dto) {

        return ResponseEntity.ok(
                buildResponse(200, "OrderItem updated successfully",
                        service.update(id, dto))
        );
    }

 
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Integer id) {

        service.delete(id);

        return ResponseEntity.ok(
                buildResponse(200, "OrderItem deleted successfully", null)
        );
    }
}