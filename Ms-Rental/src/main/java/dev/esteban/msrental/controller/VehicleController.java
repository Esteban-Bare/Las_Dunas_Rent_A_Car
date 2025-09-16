package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.VehicleBackofficeDto;
import dev.esteban.msrental.dto.VehicleDto;
import dev.esteban.msrental.dto.VehicleSearchDto;
import dev.esteban.msrental.dto.VehicleUpdateStatusDto;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("common/all")
    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(VehicleDto::new).toList();
    }

    @GetMapping("common/{id}")
    public VehicleDto getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return new VehicleDto(vehicle);
    }

    @PostMapping("common/available")
    public ResponseEntity<?> getAvailableCars(@RequestBody VehicleSearchDto vehicleSearchDto) {
        return vehicleService.getAvailableCars(vehicleSearchDto);
    }

    @GetMapping("common/count")
    public Long getVehicleCount() {
        return vehicleRepository.count();
    }

    @GetMapping("/backoffice/all")
    public ResponseEntity<?> getAllVehiclesBackoffice(@RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN") && !role.equals("MANAGER")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return ResponseEntity.ok(vehicles.stream().map(VehicleBackofficeDto::new).toList());
    }

    @PutMapping("/backoffice/update/status")
    public ResponseEntity<?> updateVehicleStatus(@RequestBody VehicleUpdateStatusDto vehicleUpdateStatusDto, @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN") && !role.equals("MANAGER")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return vehicleService.updateVehicleStatus(vehicleUpdateStatusDto);
        }
}
