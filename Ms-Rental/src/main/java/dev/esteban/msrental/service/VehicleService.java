package dev.esteban.msrental.service;

import dev.esteban.msrental.dto.*;
import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.ReservationRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.client.MsPricingFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MsPricingFeignClient msPricingFeignClient;


    public ResponseEntity<?>  getAvailableCars(VehicleSearchDto vehicleSearchDto) {
        List<Store> stores = storeService.getStoresByCity(vehicleSearchDto.getCity());
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body("No stores available in this city");
        }
        List<VehiclePerStoreDto> list = new ArrayList<>();
        stores.forEach(store -> {
            VehiclePerStoreDto vehiclePerStoreDto = new VehiclePerStoreDto();
            Map<String, List<VehicleDto>> storeVehicles = new HashMap<>();
            List<VehicleDto> vehicles = store.getVehicles().stream().filter(vehicle -> {
                boolean isAvailable = vehicleIsAvailable(vehicle);
                boolean isNotReserved = vehicleIsReservedBetweenTwoDates(vehicle, vehicleSearchDto.getStartDateHour(), vehicleSearchDto.getEndDateHour());
                return isAvailable && isNotReserved;
            }).map(vehicle -> {
                try {
                    ResponseEntity<PriceDto> response = msPricingFeignClient
                     .getPricesByCar(new VehiclePriceDto(vehicle.getId(),
                             vehicle.getCategory().getName(), vehicle.getPricePerDay(), vehicleSearchDto.getStartDateHour(), vehicleSearchDto.getEndDateHour()));
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        PriceDto pricesDto = response.getBody();
                        return new VehicleDto(vehicle.getId(), vehicle.getModel(), vehicle.getBrand().getName(), vehicle.getCategory().getName(),vehicle.getPricePerDay(), pricesDto);
                    } else {
                        return new VehicleDto(vehicle.getId(), vehicle.getModel(), vehicle.getBrand().getName(), vehicle.getCategory().getName(),vehicle.getPricePerDay(), null);
                    }
                } catch (Exception e) {
                    return new VehicleDto(vehicle.getId(), vehicle.getModel(), vehicle.getBrand().getName(), vehicle.getCategory().getName(),vehicle.getPricePerDay(), null);
                }})
                    .collect(Collectors.toList());
            storeVehicles.put(store.getName(), vehicles);
            vehiclePerStoreDto.setStoreId(store.getId());
            vehiclePerStoreDto.setStoreVehicles(storeVehicles);
            list.add(vehiclePerStoreDto);
        });
        if (list.isEmpty()) {
            return ResponseEntity.badRequest().body("No vehicles available in this city");
        }
        AvailableVehicleDto availableVehicleDto = new AvailableVehicleDto(list);
        return ResponseEntity.ok(availableVehicleDto);
    }

    public boolean vehicleIsAvailable(Vehicle vehicle) {
        return vehicle.getStatus().equals(StatusVehicle.AVAILABLE);
    }

    public boolean vehicleIsReservedBetweenTwoDates(Vehicle vehicle,LocalDateTime startDate, LocalDateTime endDate) {
        int bufferHours = 12; // 12-hour buffer before and after the reservation
        LocalDateTime bufferedStartDate = startDate.minusHours(bufferHours);
        LocalDateTime bufferedEndDate = endDate.plusHours(bufferHours);

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                vehicle.getId(),
                bufferedStartDate,
                bufferedEndDate
        );
        return overlappingReservations.isEmpty();
    }
}
