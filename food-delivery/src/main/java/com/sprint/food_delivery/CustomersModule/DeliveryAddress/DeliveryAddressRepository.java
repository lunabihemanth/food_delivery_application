package com.sprint.food_delivery.CustomersModule.DeliveryAddress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Integer> {

    // DERIVED QUERY
    List<DeliveryAddress> findByCustomer_CustomerId(Integer customerId);

    
    // CUSTOM SELECT QUERY

    // Search addresses by city
    @Query("SELECT d FROM DeliveryAddress d WHERE LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<DeliveryAddress> searchByCity(@Param("city") String city);

    //  CUSTOM MODIFY QUERY
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryAddress d SET d.addressLine1 = :line1, d.addressLine2 = :line2, d.city = :city, d.state = :state, d.postalCode = :postalCode WHERE d.addressId = :id")
    int updateAddressDetails(@Param("id") Integer id,
                            @Param("line1") String line1,
                             @Param("line2") String line2,
                             @Param("city") String city,
                             @Param("state") String state,
                             @Param("postalCode") String postalCode);
}