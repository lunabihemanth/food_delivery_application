package com.sprint.food_delivery.CustomersModule.Customers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IOrdersService orderService;

    // helper method to build response structure
    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> save(
            @Valid @RequestBody CustomerRequestDTO customerDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(201, "Customer created successfully",
                        customerService.save(customerDTO)));
    }

    // GET ALL
    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll() {

        return ResponseEntity.ok(
                buildResponse(200, "Customers fetched successfully",
                        customerService.getAll())
        );
    }

    // GET BY ID
    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> findById(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                buildResponse(200, "Customer fetched successfully",
                        customerService.findById(customerId))
        );
    }

    // UPDATE
    @PutMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Integer customerId,
            @Valid @RequestBody CustomerRequestDTO customerDTO) {

        return ResponseEntity.ok(
                buildResponse(200, "Customer updated successfully",
                        customerService.update(customerId, customerDTO))
        );
    }

    // DELETE
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Integer customerId) {

        customerService.delete(customerId);

        return ResponseEntity.ok(
                buildResponse(200, "Customer deleted successfully", null)
        );
    }

    //get customer by order
    @GetMapping("/{customerId}/orders")
        public ResponseEntity<?> getOrdersByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(
                buildResponse(200, "Customer orders fetched", orderService.getByCustomerId(customerId))
        );
}
}