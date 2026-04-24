package com.sprint.food_delivery.OrderModule.Orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.CustomersModule.Customers.CustomerRepository;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDrivers;
import com.sprint.food_delivery.DeliveryModule.DeliveryDrivers.DeliveryDriversRepository;
import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.RestaurantsModule.Restaurants.RestaurantsRepository;

@Service
public class OrdersService implements IOrdersService {

    @Autowired
    private OrdersRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    @Autowired
    private DeliveryDriversRepository driversRepository;

    // ---------- EXISTING CRUD METHODS ----------

    @Override
    public OrdersResponseDTO save(OrdersRequestDTO dto) {
        var customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        var restaurant = restaurantsRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Orders order = new Orders();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setOrderStatus("PENDING");

        if (dto.getDeliveryDriverId() != null) {
            DeliveryDrivers driver = driversRepository.findById(dto.getDeliveryDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            order.setDeliveryDriver(driver);
        }

        return map(repository.save(order));
    }

    @Override
    public List<OrdersResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public OrdersResponseDTO findById(Integer id) {
        Orders order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return map(order);
    }

    @Override
    public List<OrdersResponseDTO> getByCustomerId(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return repository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdersResponseDTO> getByRestaurantId(Integer restaurantId) {
        if (!restaurantsRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return repository.findByRestaurant_RestaurantId(restaurantId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public OrdersResponseDTO update(Integer id, OrdersRequestDTO dto) {
        Orders existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        String currentStatus = existing.getOrderStatus();
        String newStatus = dto.getOrderStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid order status transition from "
                    + currentStatus + " to " + newStatus);
        }

        existing.setOrderStatus(newStatus);
        return map(repository.save(existing));
    }

    @Override
    public String delete(Integer id) {
        Orders order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if ("DELIVERED".equals(order.getOrderStatus())) {
            throw new BadRequestException("Delivered orders cannot be deleted");
        }

        repository.delete(order);
        return "Order deleted successfully with id: " + id;
    }

    // ---------- NEW DELIVERY ASSIGNMENT METHODS ----------

    @Override
    public OrdersResponseDTO assignDriver(Integer orderId, Integer driverId) {
        Orders order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        DeliveryDrivers driver = driversRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        if ("DELIVERED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new BadRequestException("Cannot assign driver to a " + order.getOrderStatus().toLowerCase() + " order");
        }

        order.setDeliveryDriver(driver);
        // Optionally set status to "DRIVER_ASSIGNED" if you want
        return map(repository.save(order));
    }

    @Override
    public OrdersResponseDTO updateDeliveryStatus(Integer orderId, String status) {
        Orders order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!"OUT_FOR_DELIVERY".equals(status) && !"DELIVERED".equals(status)) {
            throw new BadRequestException("Invalid delivery status. Allowed: OUT_FOR_DELIVERY, DELIVERED");
        }

        String currentStatus = order.getOrderStatus();
        if (!isValidTransition(currentStatus, status)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + status);
        }

        order.setOrderStatus(status);

        if ("DELIVERED".equals(status)) {
            // Optional: set delivered timestamp field if you add one
            // order.setDeliveredAt(LocalDateTime.now());
        }

        return map(repository.save(order));
    }


    // ---------- HELPER METHODS ----------

    private boolean isValidTransition(String current, String next) {
        if (current == null || next == null) return false;

        switch (current) {
            case "PENDING":
                return "CONFIRMED".equals(next) || "CANCELLED".equals(next);
            case "CONFIRMED":
                return "OUT_FOR_DELIVERY".equals(next) || "CANCELLED".equals(next);
            case "OUT_FOR_DELIVERY":
                return "DELIVERED".equals(next);
            default:
                return false;
        }
    }
    

    @Override
    public List<OrdersResponseDTO> getOrdersByDriver(Integer driverId) {
        if (!driversRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver not found with id: " + driverId);
        }
        return repository.findOrdersByDriverId(driverId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private OrdersResponseDTO map(Orders o) {
        return new OrdersResponseDTO(
                o.getOrderId(),
                o.getOrderDate(),
                o.getCustomer().getCustomerId(),
                o.getRestaurant().getRestaurantId(),
                o.getDeliveryDriver() != null ? o.getDeliveryDriver().getDriverId() : null,
                o.getOrderStatus()
        );
    }
}