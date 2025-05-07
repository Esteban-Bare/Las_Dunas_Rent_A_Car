package dev.esteban.msrental.service;

import dev.esteban.msrental.dto.*;
import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.client.MsPricingFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MsPricingFeignClient msPricingFeignClient;


    public ResponseEntity<?>  getAvailableCars(VehicleSearchDto vehicleSearchDto) {
        List<Store> stores = storeService.getStoresByCity(vehicleSearchDto.getCity());
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body("No stores available in this city");
        }
        Map<String, List<VehicleDto>> storeVehicles = new HashMap<>();
        stores.forEach(store -> {
            List<VehicleDto> vehicles = store.getVehicles().stream().filter(vehicle -> {
                boolean isAvailable = vehicleIsAvailable(vehicle);
                boolean isReserved = vehicleIsReservedBetweenTwoDates(vehicle, vehicleSearchDto.getStartDateHour(), vehicleSearchDto.getEndDateHour());
                return isAvailable && !isReserved;
            }).map(vehicle -> {
                    ResponseEntity<PriceDto> response = msPricingFeignClient.getPricesByCar(new VehiclePriceDto(vehicle.getId(), vehicle.getCategory().getName(),vehicle.getPricePerDay(), vehicleSearchDto.getStartDateHour(), vehicleSearchDto.getEndDateHour()));
                    if (response.getStatusCode().isError()) {
                        return null;
                    }
                    PriceDto pricesDto = response.getBody();
                    return new VehicleDto(vehicle.getModel(), vehicle.getBrand().getName(), vehicle.getCategory().getName(),vehicle.getPricePerDay(), pricesDto);
                    })
                    .collect(Collectors.toList());
            storeVehicles.put(store.getName(), vehicles);
        });
        if (storeVehicles.isEmpty()) {
            return ResponseEntity.badRequest().body("No vehicles available in this city");
        }
        AvailableVehicleDto availableVehicleDto = new AvailableVehicleDto(storeVehicles);
        return ResponseEntity.ok(availableVehicleDto);
    }

    public boolean vehicleIsAvailable(Vehicle vehicle) {
        return vehicle.getStatus().equals(StatusVehicle.AVAILABLE);
    }

    public boolean vehicleIsReservedBetweenTwoDates(Vehicle vehicle,LocalDateTime startDate, LocalDateTime endDate) {
        return vehicle.getReservations().stream().anyMatch(reservation -> {
            LocalDateTime reservationStartDate = reservation.getRequested_start_date();
            LocalDateTime reservationEndDate = reservation.getRequested_end_date();
            return (startDate.isAfter(reservationStartDate) && startDate.isBefore(reservationEndDate)) ||
                    (endDate.isAfter(reservationStartDate) && endDate.isBefore(reservationEndDate));
        });
    }
}
