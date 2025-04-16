package dev.esteban.msrental.controller;

import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rental/vehicle")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping("/all")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @PostMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }
}
