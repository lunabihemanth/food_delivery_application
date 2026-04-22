package com.sprint.food_delivery.RestaurantsModule.Restaurants;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RestaurantsRepository extends JpaRepository<Restaurants, Integer> {

    //  Derived
    boolean existsByRestaurantName(String restaurantName);

    Optional<Restaurants> findByRestaurantName(String restaurantName);

    //  Custom SELECT Query
    @Query("SELECT r FROM Restaurants r WHERE LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Restaurants> searchByName(@Param("name") String name);

    //  Custom MODIFY Query
    @Modifying
    @Transactional
    @Query("UPDATE Restaurants r SET r.restaurantName = :name, r.restaurantAddress = :address, r.restaurantPhone = :phone WHERE r.restaurantId = :id")
    int updateRestaurant(@Param("id") Integer id,
                         @Param("name") String name,
                         @Param("address") String address,
                         @Param("phone") String phone);
}