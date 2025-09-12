package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.NewReservationDto;
import dev.esteban.msrental.model.Reservation;
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
    public ResponseEntity<?> createReservation(@RequestBody NewReservationDto newReservationDto) {
        return reservationService.createReservation(newReservationDto);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id).isPresent() ? reservationService.getReservationById(id).get() : null;
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(@PathVariable Integer userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }
}
