package com.sprint.food_delivery.OrderModule.OrderItems;

import java.util.List;

import com.sprint.food_delivery.OrderModule.Orders.OrdersResponseDTO;

public interface IOrderItemsService {
    OrderItemsResponseDTO save(OrderItemsRequestDTO dto);
    List<OrderItemsResponseDTO> getAll();                          // might still be useful for admin
    OrderItemsResponseDTO findById(Integer id);
    OrderItemsResponseDTO update(Integer id, OrderItemsRequestDTO dto);  // kept but not exposed via new API
    List<OrderItemsResponseDTO> getByOrderId(Integer orderId);
    String delete(Integer id);
    
    // New – quantity‑only update
    OrderItemsResponseDTO updateQuantity(Integer orderItemId, Integer quantity);
    OrdersResponseDTO updateDeliveryStatus(Integer orderId, String status);
}