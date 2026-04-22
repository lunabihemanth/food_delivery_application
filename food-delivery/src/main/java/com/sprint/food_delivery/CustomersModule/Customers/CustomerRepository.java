package com.sprint.food_delivery.CustomersModule.Customers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customers, Integer> {

    // Derived Queries

    // Check if email already exists (for validation)
    boolean existsByCustomerEmail(String customerEmail);
    
    // Find customer by email
    Optional<Customers> findByCustomerEmail(String customerEmail);

    // CUSTOM SELECT QUERIES

    // Search customers by name (case-insensitive)
    @Query("SELECT c FROM Customers c WHERE LOWER(c.customerName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customers> searchByName(@Param("name") String name);


    // MODIFY (UPDATE) QUERIES
    @Modifying
    @Transactional
    @Query("UPDATE Customers c SET c.customerName = :name, c.customerEmail = :email, c.customerPhone = :phone WHERE c.customerId = :id")
    int updateCustomerFullDetails(@Param("id") Integer id,
                              @Param("name") String name,
                              @Param("email") String email,
                              @Param("phone") String phone);

}