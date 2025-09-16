package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePerStoreDto {
    private Map<String, List<VehicleDto>> storeVehicles;
    private Long storeId;
}
