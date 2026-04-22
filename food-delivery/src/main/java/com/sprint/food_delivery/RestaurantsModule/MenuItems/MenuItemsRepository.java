package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MenuItemsRepository extends JpaRepository<MenuItems, Integer> {

    // DERIVED QUERY
    List<MenuItems> findByRestaurant_RestaurantId(Integer restaurantId);



    // CUSTOM SELECT QUERY
    @Query("SELECT m FROM MenuItems m WHERE LOWER(m.itemName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MenuItems> searchByItemName(@Param("name") String name);

 

    // CUSTOM MODIFY QUERY
    @Modifying
    @Transactional
    @Query("UPDATE MenuItems m SET m.itemPrice = :price WHERE m.itemId = :id")
    int updateItemPrice(@Param("id") Integer id,
                        @Param("price") Double price);
}