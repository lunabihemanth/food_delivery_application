package com.sprint.food_delivery.DeliveryModule.DeliveryDrivers;

public class DeliveryDriversResponseDTO {

    private Integer driverId;
    private String driverName;
    private String driverPhone;
    private String driverVehicle;

    public DeliveryDriversResponseDTO(Integer driverId, String driverName,
                                      String driverPhone, String driverVehicle) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverVehicle = driverVehicle;
    }

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