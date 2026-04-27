package com.sprint.food_delivery.CustomersModule.Customers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// This class is used to receive customer input from API request
public class CustomerRequestDTO {
    
    @NotBlank(message = "Customer name cannot be empty")
    @Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
    private String customerName;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Phone cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String customerPhone;

    

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }


    
}