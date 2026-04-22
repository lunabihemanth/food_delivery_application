package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DeliveryDriversRepository extends JpaRepository<DeliveryDrivers, Integer> {

    //  Derived
    boolean existsByDriverPhone(String driverPhone);

    Optional<DeliveryDrivers> findByDriverPhone(String driverPhone);


    // Custom SELECT
    @Query("SELECT d FROM DeliveryDrivers d WHERE LOWER(d.driverName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<DeliveryDrivers> searchByName(@Param("name") String name);

  

    // Custom MODIFY
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryDrivers d SET d.driverVehicle = :vehicle WHERE d.driverId = :id")
    int updateDriverVehicle(@Param("id") Integer id,
                            @Param("vehicle") String vehicle);
}