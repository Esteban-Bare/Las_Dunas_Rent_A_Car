package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.RentalDto;
import dev.esteban.msrental.enums.RentalStatus;
import dev.esteban.msrental.model.Rental;
import dev.esteban.msrental.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/create/{reservationId}")
    public ResponseEntity<?> createRentalFromReservation(@PathVariable Long reservationId , @RequestHeader("X-User-Role") String role) {
        if (!role.equals("MANAGER") && !role.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        try {
            Rental rental = rentalService.createRentalFromReservation(reservationId);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        try {
            Rental rental = rentalService.getRentalById(id);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Rental not found: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<RentalDto>> getUserRentals(@RequestHeader("X-User-Id") String id) {
        List<Rental> rentals = rentalService.getRentalsByUserId(Integer.valueOf(id));
        return ResponseEntity.ok(rentals.stream().map(RentalDto::new).toList());
    }

    @GetMapping("/all")
    public ResponseEntity<List<RentalDto>> getAllRentals(@RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN") && !role.equals("MANAGER")) {
            return ResponseEntity.status(403).body(null);
        }
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals.stream().map(RentalDto::new).toList());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Rental>> getRentalsByStatus(@PathVariable RentalStatus status) {
        List<Rental> rentals = rentalService.getRentalsByStatus(status);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        List<Rental> activeRentals = rentalService.getRentalsByStatus(RentalStatus.IN_PROGRESS);
        return ResponseEntity.ok(activeRentals);
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveRentals() {
        Long count = rentalService.countRentalsByStatus(RentalStatus.IN_PROGRESS);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeRental(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if (!role.equals("MANAGER") && !role.equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        try {
            return ResponseEntity.ok(rentalService.completeRental(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnVehicle(@PathVariable Long id) {
        try {
            Rental rental = rentalService.returnVehicle(id);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRental(@PathVariable Long id) {
        try {
            Rental rental = rentalService.cancelRental(id);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/check-overdue")
    public ResponseEntity<?> checkOverdueRentals() {
        try {
            rentalService.checkOverdueRentals();
            return ResponseEntity.ok("Overdue rentals checked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}