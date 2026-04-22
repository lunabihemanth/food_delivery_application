package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "delivery_drivers")
public class DeliveryDrivers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_phone")
    private String driverPhone;

    @Column(name = "driver_vehicle")
    private String driverVehicle;

    // Getters and Setters

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

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