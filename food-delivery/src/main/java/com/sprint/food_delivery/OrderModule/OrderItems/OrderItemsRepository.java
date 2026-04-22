package com.sprint.food_delivery.OrderModule.OrderItems;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {

    //Derived
    boolean existsByOrderItemId(Integer orderItemId);

    List<OrderItems> findByOrder_OrderId(Integer orderId);
    

   //custom Query
    @Query("SELECT oi FROM OrderItems oi WHERE LOWER(oi.menuItem.itemName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OrderItems> searchByItemName(@Param("name") String name);

   //Update Query
    @Modifying
    @Transactional
    @Query("UPDATE OrderItems oi SET oi.quantity = :quantity WHERE oi.orderItemId = :id")
    int updateQuantity(@Param("id") Integer id,
                       @Param("quantity") Integer quantity);
}