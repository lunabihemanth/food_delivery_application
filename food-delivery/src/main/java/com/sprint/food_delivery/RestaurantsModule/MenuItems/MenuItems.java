package com.sprint.food_delivery.RestaurantsModule.MenuItems;

import com.sprint.food_delivery.RestaurantsModule.Restaurants.Restaurants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
public class MenuItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "item_price")
    private Double itemPrice;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurants restaurant;

    // Getters and Setters    

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

    public Restaurants getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurants restaurant) {
        this.restaurant = restaurant;
    }
}