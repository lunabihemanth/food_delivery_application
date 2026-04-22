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
    private IOrdersService service;

    private Map<String, Object> build(int s, String m, Object d) {
        Map<String, Object> r = new HashMap<>();
        r.put("status", s);
        r.put("message", m);
        r.put("data", d);
        r.put("timestamp", LocalDateTime.now());
        return r;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody OrdersRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(build(201, "Order created", service.save(dto)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(build(200, "Orders fetched", service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(build(200, "Order fetched", service.findById(id)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(build(200, "Orders fetched",
                service.getByCustomerId(customerId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                   @Valid @RequestBody OrdersRequestDTO dto) {
        return ResponseEntity.ok(build(200, "Order updated",
                service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(build(200, "Order deleted", null));
    }
}