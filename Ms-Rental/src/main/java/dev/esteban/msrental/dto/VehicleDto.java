package dev.esteban.msrental.dto;

import dev.esteban.msrental.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private Long vehicleId;
    private String model;
    private String brand;
    private String category;
    private BigDecimal pricePerDay;
    private PriceDto priceDto;

    public VehicleDto(Vehicle vehicle) {
        this.vehicleId = vehicle.getId();
        this.model = vehicle.getModel();
        this.brand = vehicle.getBrand().getName();
        this.category = vehicle.getCategory().getName();
        this.pricePerDay = vehicle.getPricePerDay();
        this.priceDto = null;
    }
}
