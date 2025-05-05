package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private String model;
    private String brand;
    private String category;
    private BigDecimal pricePerDay;
}
