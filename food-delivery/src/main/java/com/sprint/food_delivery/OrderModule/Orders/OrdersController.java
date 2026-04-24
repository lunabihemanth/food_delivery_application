package com.sprint.food_delivery.OrderModule.Orders;

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
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    private Map<String, Object> build(int s, String m, Object d) {
        Map<String, Object> r = new HashMap<>();
        r.put("status", s);
        r.put("message", m);
        r.put("data", d);
        r.put("timestamp", LocalDateTime.now());
        return r;
    }

    // ---------- EXISTING ORDER ENDPOINTS ----------

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody OrdersRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(build(201, "Order created", ordersService.save(dto)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(build(200, "Orders fetched", ordersService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(build(200, "Order fetched", ordersService.findById(id)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(build(200, "Orders fetched", ordersService.getByCustomerId(customerId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody OrdersRequestDTO dto) {
        return ResponseEntity.ok(build(200, "Order updated", ordersService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        ordersService.delete(id);
        return ResponseEntity.ok(build(200, "Order deleted", null));
    }

    // ---------- DELIVERY ASSIGNMENT ENDPOINTS ----------

    @PutMapping("/{orderId}/assign-driver/{driverId}")
    public ResponseEntity<?> assignDriver(@PathVariable Integer orderId,
                                          @PathVariable Integer driverId) {
        OrdersResponseDTO result = ordersService.assignDriver(orderId, driverId);
        return ResponseEntity.ok(build(200, "Driver assigned successfully", result));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId,
                                            @RequestBody Map<String, String> statusRequest) {
        String status = statusRequest.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(build(400, "Status field is required", null));
        }
        OrdersResponseDTO result = ordersService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(build(200, "Order status updated", result));
    }

    @PutMapping("/{orderId}/delivery-status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Integer orderId,
                                                @RequestBody Map<String, String> statusRequest) {
        String status = statusRequest.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(build(400, "Status field is required", null));
        }
        OrdersResponseDTO result = ordersService.updateDeliveryStatus(orderId, status);
        return ResponseEntity.ok(build(200, "Delivery status updated", result));
    }

    // NOTE: Endpoint to get orders by driver – use a distinct path to avoid conflict with /{id}
    // The official spec expects this in the drivers controller: GET /drivers/{driverId}/orders
    // But if you must keep it here, use a unique path like:
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<?> getOrdersByDriver(@PathVariable Integer driverId) {
        return ResponseEntity.ok(build(200, "Driver orders fetched", 
                ordersService.getOrdersByDriver(driverId)));
    }
}