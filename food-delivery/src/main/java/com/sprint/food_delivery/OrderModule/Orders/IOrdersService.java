    package com.sprint.food_delivery.OrderModule.Orders;

    import java.util.List;

    public interface IOrdersService {
        OrdersResponseDTO save(OrdersRequestDTO dto);
        List<OrdersResponseDTO> getAll();
        OrdersResponseDTO findById(Integer id);
        List<OrdersResponseDTO> getByCustomerId(Integer customerId);
        OrdersResponseDTO update(Integer id, OrdersRequestDTO dto);
        String delete(Integer id);
        List<OrdersResponseDTO> getByRestaurantId(Integer restaurantId);

        // Delivery assignment methods
        OrdersResponseDTO assignDriver(Integer orderId, Integer driverId);
        OrdersResponseDTO updateDeliveryStatus(Integer orderId, String status);
        List<OrdersResponseDTO> getOrdersByDriver(Integer driverId);

        OrdersResponseDTO updateOrderStatus(Integer orderId, String status);

        

        
    }