package com.sprint.food_delivery.OrderModule.OrderItems;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint.food_delivery.Exception.BadRequestException;
import com.sprint.food_delivery.Exception.ResourceNotFoundException;
import com.sprint.food_delivery.OrderModule.Orders.Orders;
import com.sprint.food_delivery.OrderModule.Orders.OrdersRepository;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItems;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItemsRepository;

@Service
public class OrderItemsService implements IOrderItemsService {

    @Autowired
    private OrderItemsRepository repository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private MenuItemsRepository menuItemsRepository;

    // CREATE
    @Override
    public OrderItemsResponseDTO save(OrderItemsRequestDTO dto) {

        validateQuantity(dto.getQuantity());

        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        MenuItems item = menuItemsRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + dto.getItemId()));

        validateSameRestaurant(order, item);

        OrderItems entity = new OrderItems();
        entity.setQuantity(dto.getQuantity());
        entity.setOrder(order);
        entity.setMenuItem(item);

        return map(repository.save(entity));
    }

    // GET ALL
    @Override
    public List<OrderItemsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public OrderItemsResponseDTO findById(Integer id) {

        OrderItems entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));

        return map(entity);
    }

    // GET BY ORDER ID (FIXED + MATCHING INTERFACE)
    @Override
    public List<OrderItemsResponseDTO> getByOrderId(Integer orderId) {

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return repository.findByOrder_OrderId(order.getOrderId())
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Override
    public OrderItemsResponseDTO update(Integer id, OrderItemsRequestDTO dto) {

        OrderItems existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));

        validateQuantity(dto.getQuantity());

        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        MenuItems item = menuItemsRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + dto.getItemId()));

        validateSameRestaurant(order, item);

        existing.setQuantity(dto.getQuantity());
        existing.setOrder(order);
        existing.setMenuItem(item);

        return map(repository.save(existing));
    }

    // DELETE
    @Override
    public String delete(Integer id) {

        OrderItems existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));

        repository.delete(existing);

        return "Order item deleted successfully with id: " + id;
    }

    //helpers
    private void validateQuantity(Integer qty) {
        if (qty == null || qty <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
    }

    private void validateSameRestaurant(Orders order, MenuItems item) {
        if (!order.getRestaurant().getRestaurantId()
                .equals(item.getRestaurant().getRestaurantId())) {
            throw new BadRequestException("Cannot mix items from different restaurants in same order");
        }
    }

    private OrderItemsResponseDTO map(OrderItems oi) {
        return new OrderItemsResponseDTO(
                oi.getOrderItemId(),
                oi.getQuantity(),
                oi.getOrder().getOrderId(),
                oi.getMenuItem().getItemId()
        );
    }
}