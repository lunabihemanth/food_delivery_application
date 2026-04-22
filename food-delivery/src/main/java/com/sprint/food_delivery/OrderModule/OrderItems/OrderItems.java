package com.sprint.food_delivery.OrderModule.OrderItems;

import com.sprint.food_delivery.OrderModule.Orders.Orders;
import com.sprint.food_delivery.RestaurantsModule.MenuItems.MenuItems;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity

public class OrderItems {

	@Id
    @Column(name = "order_item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

	@NotNull
	@Min(value = 1, message = "Quantity must be atleast 1.")
	private Integer quantity;

	@ManyToOne
	@JoinColumn(name = "order_id")
	@NotNull(message = "order is required.")
	private Orders order;

	@ManyToOne
	@JoinColumn(name = "item_id")
	@NotNull(message = "Menu Item is Required.")
	private MenuItems menuItem;

	// Getters and Setters

	public Integer getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Orders getOrder() {
		return order;
	}

	public void setOrder(Orders order) {
		this.order = order;
	}

	public MenuItems getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItems menuItem) {
		this.menuItem = menuItem;
	}

}
