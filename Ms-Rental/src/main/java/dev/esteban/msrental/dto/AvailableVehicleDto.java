package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableVehicleDto {
    Map<String, List<VehicleDto>> vehicles;
}

