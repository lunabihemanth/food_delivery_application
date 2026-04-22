package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenuItemsRequestDTO {

    @NotBlank(message = "Item name cannot be empty")
    private String itemName;

    private String itemDescription;

    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double itemPrice;
    

    @NotNull(message = "Restaurant ID cannot be null")
    private Integer restaurantId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }


 
}