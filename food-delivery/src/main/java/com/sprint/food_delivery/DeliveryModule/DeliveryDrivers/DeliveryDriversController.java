package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;

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

import com.sprint.food_delivery.OrderModule.Orders.IOrdersService;

import jakarta.validation.Valid;
//@RestController = @Controller + @ResponseBody
//This class will send data directly to the user, not a webpage.
@RestController
@RequestMapping("/drivers") //It is just a base URL (starting path) for your API
public class DeliveryDriversController {

    @Autowired
    private IDeliveryDriversService service;

    @Autowired
    private IOrdersService ordersService;   // ✅ Inject the orders service

    private Map<String, Object> build(int s, String m, Object d) {
        Map<String, Object> r = new HashMap<>();
        r.put("status", s);
        r.put("message", m);
        r.put("data", d);
        //Timestamp = current date + current time
        r.put("timestamp", LocalDateTime.now());
        return r;
    }

    @PostMapping
    //ResponseEntity<?> here <?> means any type of data
    //@valid = Check the input data before using it
    //@RequestBody = takes JSON data from request and converts it into a Java object
    //ResponseEntity = a Spring class used to send response (data + HTTP status) back to the client
    public ResponseEntity<?> create(@Valid  @RequestBody DeliveryDriversRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(build(201, "Driver created", service.save(dto)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(build(200, "Drivers fetched", service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(build(200, "Driver fetched", service.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                   @Valid @RequestBody DeliveryDriversRequestDTO dto) {
        return ResponseEntity.ok(build(200, "Driver updated", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(build(200, "Driver deleted", null));
    }

    // ✅ GET /drivers/{driverId}/orders - list orders assigned to driver
    @GetMapping("/{driverId}/orders")
    public ResponseEntity<?> getDriverOrders(@PathVariable Integer driverId) {
        // Call instance method, not static
        return ResponseEntity.ok(build(200, "Orders fetched", 
                ordersService.getOrdersByDriver(driverId)));
    }
}