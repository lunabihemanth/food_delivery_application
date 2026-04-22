package com.sprint.food_delivery.OrderModule.OrderItems;

import java.util.List;

public interface IOrderItemsService {
    OrderItemsResponseDTO save(OrderItemsRequestDTO dto);
    List<OrderItemsResponseDTO> getAll();
    OrderItemsResponseDTO findById(Integer id);
    OrderItemsResponseDTO update(Integer id, OrderItemsRequestDTO dto);
    List<OrderItemsResponseDTO> getByOrderId(Integer orderId);
    String delete(Integer id);
}