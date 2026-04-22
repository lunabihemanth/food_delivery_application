package com.sprint.food_delivery.OrderModule.Orders;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	//derived
    List<Orders> findByCustomer_CustomerId(Integer customerId);
    List<Orders> findByRestaurant_RestaurantId(Integer restaurantId);
    


    //custom Query
    @Query("SELECT o FROM Orders o WHERE o.orderStatus = :status")
    List<Orders> findByStatus(@Param("status") String status);

    //update Query
    @Modifying
    @Transactional
    @Query("UPDATE Orders o SET o.orderStatus = :status WHERE o.orderId = :id")
    int updateOrderStatus(@Param("id") Integer id,
                          @Param("status") String status);
}