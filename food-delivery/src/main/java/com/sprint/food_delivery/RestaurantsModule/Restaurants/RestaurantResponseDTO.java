package com.sprint.food_delivery.RestaurantsModule.Restaurants;

public class RestaurantResponseDTO {
	private Integer restaurantId;
	private String restaurantName;
	private String restaurantAddress;
	private String restaurantPhone;
	public RestaurantResponseDTO(Integer restaurantId, String restaurantName, String restaurantAddress,
			String restaurantPhone) {
		this.restaurantId = restaurantId;
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.restaurantPhone = restaurantPhone;
	}
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}
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
