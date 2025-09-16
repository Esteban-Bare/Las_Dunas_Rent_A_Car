package dev.esteban.msrental.dto;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleBackofficeDto {
    private Long vehicleId;
    private String model;
    private String brand;
    private String category;
    private String plateNumber;
    private BigDecimal pricePerDay;
    private StatusVehicle status;

    public VehicleBackofficeDto(Vehicle vehicle) {
        this.vehicleId = vehicle.getId();
        this.model = vehicle.getModel();
        this.brand = vehicle.getBrand().getName();
        this.category = vehicle.getCategory().getName();
        this.plateNumber = vehicle.getPlateNumber();
        this.pricePerDay = vehicle.getPricePerDay();
        this.status = vehicle.getStatus();
    }
}
