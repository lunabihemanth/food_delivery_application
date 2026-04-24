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
import org.springframework.web.bind.annotation.RestController;

import com.sprint.food_delivery.OrderModule.Orders.OrdersResponseDTO;

import jakarta.validation.Valid;

    @RestController
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

    // ✅ POST /orders/{orderId}/items
    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<Map<String, Object>> addItemToOrder(
            @PathVariable Integer orderId,
            @Valid @RequestBody OrderItemsRequestDTO dto) {
        dto.setOrderId(orderId);      // <-- crucial: set from path before saving
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "Item added to order", service.save(dto)));
    }

    // ✅ GET /orders/{orderId}/items
    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<Map<String, Object>> getItemsByOrder(
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(
                buildResponse(200, "Order items fetched", service.getByOrderId(orderId)));
    }

    // ✅ PUT /order-items/{orderItemId} – update quantity only
    @PutMapping("/order-items/{orderItemId}")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @PathVariable Integer orderItemId,
            @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest()
                    .body(buildResponse(400, "Quantity is required", null));
        }
        return ResponseEntity.ok(
                buildResponse(200, "Quantity updated", service.updateQuantity(orderItemId, quantity)));
    }

    // ✅ DELETE /order-items/{orderItemId}
    @DeleteMapping("/order-items/{orderItemId}")
    public ResponseEntity<Map<String, Object>> removeItem(
            @PathVariable Integer orderItemId) {
        service.delete(orderItemId);
        return ResponseEntity.ok(buildResponse(200, "Order item removed", null));
    }

    // optional: you can keep a GET for a single order item if you need to pre‑fill the edit form
    @GetMapping("/order-items/{orderItemId}")
    public ResponseEntity<Map<String, Object>> getOrderItem(
            @PathVariable Integer orderItemId) {
        return ResponseEntity.ok(
                buildResponse(200, "Order item fetched", service.findById(orderItemId)));
    }

    @PutMapping("/{orderId}/delivery-status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Integer orderId,
                                                @RequestBody Map<String, String> statusRequest) {
        String status = statusRequest.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(buildResponse(400, "Status field is required", null));
        }
        OrdersResponseDTO result = service.updateDeliveryStatus(orderId, status);
        return ResponseEntity.ok(buildResponse(200, "Delivery status updated", result));
    }
}