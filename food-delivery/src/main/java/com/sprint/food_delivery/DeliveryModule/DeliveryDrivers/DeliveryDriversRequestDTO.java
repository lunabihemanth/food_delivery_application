package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DeliveryDriversRequestDTO {

    @NotBlank(message = "Driver name cannot be empty")
    private String driverName;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String driverPhone;

    @NotBlank(message = "Vehicle cannot be empty")
    private String driverVehicle;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverVehicle() {
        return driverVehicle;
    }

    public void setDriverVehicle(String driverVehicle) {
        this.driverVehicle = driverVehicle;
    }

    
}