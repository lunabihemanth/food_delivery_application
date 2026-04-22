package com.sprint.food_delivery.RestaurantsModule.MenuItems;

public class MenuItemsResponseDTO {

    private Integer itemId;
    private String itemName;
    private String itemDescription;
    private Double itemPrice;
    private Integer restaurantId;

    public MenuItemsResponseDTO(Integer itemId, String itemName,
                                String itemDescription, Double itemPrice,
                                Integer restaurantId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.restaurantId = restaurantId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

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