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

    // CREATE ORDER
    @Override
    public OrdersResponseDTO save(OrdersRequestDTO dto) {

        // Validate customer
        var customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Validate restaurant
        var restaurant = restaurantsRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Orders order = new Orders();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(customer);
        order.setRestaurant(restaurant);

        // Default status (never trust client)
        order.setOrderStatus("PENDING");

        // driver assignment
        if (dto.getDeliveryDriverId() != null) {
            DeliveryDrivers driver = driversRepository.findById(dto.getDeliveryDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            order.setDeliveryDriver(driver);
        }

        return map(repository.save(order));
    }

    // GET ALL
    @Override
    public List<OrdersResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public OrdersResponseDTO findById(Integer id) {
        Orders order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return map(order);
    }

    // GET BY CUSTOMER
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

    // GET BY RESTAURANT 
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

    // UPDATE ORDER STATUS 
    @Override
    public OrdersResponseDTO update(Integer id, OrdersRequestDTO dto) {

        Orders existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        String currentStatus = existing.getOrderStatus();
        String newStatus = dto.getOrderStatus();

        // STATUS FLOW CONTROL 
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid order status transition from " 
                    + currentStatus + " to " + newStatus);
        }

        existing.setOrderStatus(newStatus);

        return map(repository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        Orders order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // cannot delete delivered orders
        if ("DELIVERED".equals(order.getOrderStatus())) {
            throw new BadRequestException("Delivered orders cannot be deleted");
        }

        repository.delete(order);

        return "Order deleted successfully with id: " + id;
    }

    // status transition rule
    private boolean isValidTransition(String current, String next) {

        if (current.equals("PENDING") && next.equals("CONFIRMED")) return true;

        if (current.equals("CONFIRMED") &&
                (next.equals("OUT_FOR_DELIVERY") || next.equals("CANCELLED"))) return true;

        if (current.equals("OUT_FOR_DELIVERY") &&
                next.equals("DELIVERED")) return true;

        return false;
    }

    // MAPPER
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