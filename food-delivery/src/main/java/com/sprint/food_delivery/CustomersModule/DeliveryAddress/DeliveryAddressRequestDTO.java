package com.sprint.food_delivery.CustomersModule.DeliveryAddress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DeliveryAddressRequestDTO {

    @NotBlank(message = "Address Line 1 cannot be empty")
    @Size(min = 5, max = 200, message = "Address Line 1 must be between 5 and 200 characters")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "State cannot be empty")
    private String state;

    @NotBlank(message = "Postal code cannot be empty")
    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    private String postalCode;

   
    private Integer customerId;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}