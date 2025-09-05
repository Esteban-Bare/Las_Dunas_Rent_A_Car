package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.VehicleSearchDto;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental/vehicle")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/all")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @PostMapping("/available")
    public ResponseEntity<?> getAvailableCars(@RequestBody VehicleSearchDto vehicleSearchDto) {
        return vehicleService.getAvailableCars(vehicleSearchDto);
    }
}
