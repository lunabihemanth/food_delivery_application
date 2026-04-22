package com.sprint.food_delivery.RestaurantsModule.Restaurants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RestaurantsRequestDTO {

    @NotBlank(message = "Restaurant name cannot be empty")
    @Size(min = 2, max = 100)
    private String restaurantName;

    @NotBlank(message = "Address cannot be empty")
    private String restaurantAddress;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String restaurantPhone;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }
}