package dev.esteban.msrental.dto;

import dev.esteban.msrental.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReservationDto {
    private Long vehicleId;
    private String model;
    private String brand;
    private String category;
    private String plateNumber;
    private String pricePerDay;

    public VehicleReservationDto(Vehicle vehicle) {
        this.vehicleId = vehicle.getId();
        this.model = vehicle.getModel();
        this.brand = vehicle.getBrand().getName();
        this.category = vehicle.getCategory().getName();
        this.plateNumber = vehicle.getPlateNumber();
        this.pricePerDay = vehicle.getPricePerDay().toString();
    }
}
