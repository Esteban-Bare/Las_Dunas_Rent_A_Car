package dev.esteban.msrental.dto;

import dev.esteban.msrental.enums.StatusVehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleUpdateStatusDto {
    private String vehicleId;
    private StatusVehicle status;
}
