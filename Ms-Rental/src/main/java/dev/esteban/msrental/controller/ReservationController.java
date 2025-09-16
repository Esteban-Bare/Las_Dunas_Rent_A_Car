package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.NewReservationDto;
import dev.esteban.msrental.dto.ReservationDto;
import dev.esteban.msrental.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@RequestHeader("X-User-Id") String id,@RequestBody NewReservationDto newReservationDto) {
        newReservationDto.setUserId(Integer.parseInt(id));
        return reservationService.createReservation(newReservationDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id).isPresent() ? ResponseEntity.ok(new ReservationDto(reservationService.getReservationById(id).get())) : ResponseEntity.badRequest().body("Reservation not found");
    }

    @GetMapping("/user")
    public List<ReservationDto> getUserReservations(@RequestHeader("X-User-Id") String id) {
        return reservationService.getReservationsByUserId(Integer.parseInt(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Integer id) {
        System.out.println("Canceling reservation with ID: " + id);
        return reservationService.cancelReservation(Long.valueOf(id));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllReservations(@RequestHeader("X-User-Role") String role) {
        if (!role.equals("ADMIN") && !role.equals("MANAGER")) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return ResponseEntity.ok(reservationService.getAllReservationsAdmin());
    }
}
