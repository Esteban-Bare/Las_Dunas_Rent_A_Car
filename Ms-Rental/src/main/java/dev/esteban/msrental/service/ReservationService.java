package dev.esteban.msrental.service;

import dev.esteban.msrental.dto.NewReservationDto;
import dev.esteban.msrental.enums.PaymentType;
import dev.esteban.msrental.enums.ReservationStatus;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.ReservationRepository;
import dev.esteban.msrental.repository.StoreRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    // Implement the logic for reservation management here
    // This could include methods for creating, updating, deleting, and retrieving reservations
    // For example:
    // public Reservation createReservation(Reservation reservation) { ... }
    // public Reservation updateReservation(Long id, Reservation reservation) { ... }
    // public void deleteReservation(Long id) { ... }
    // public List<Reservation> getAllReservations() { ... }
    // public Reservation getReservationById(Long id) { ... }

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PaymentService paymentService;

    @Transactional
    public ResponseEntity<?> createReservation(NewReservationDto newReservationDto) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(newReservationDto.getVehicleId());
            if (vehicle.isEmpty()) {
                return ResponseEntity.badRequest().body("Vehicle not found");
            }
            Optional<Store> store = storeRepository.findById(newReservationDto.getStoreId());
            if (store.isEmpty()) {
                return ResponseEntity.badRequest().body("Store not found");
            }
            boolean isAvailable = isVehicleAvailable(
                newReservationDto.getVehicleId(),
                newReservationDto.getRequestedStartDate(),
                newReservationDto.getRequestedEndDate()
            );
            if (!isAvailable) {
                return ResponseEntity.badRequest().body("Vehicle is not available for the requested dates");
            }
            Reservation reservation = new Reservation(newReservationDto.getUserId(), vehicle.get(), store.get(), newReservationDto.getRequestedStartDate(),
                    newReservationDto.getRequestedEndDate(), newReservationDto.getReservationPrice(), newReservationDto.getInsurancePrice(), ReservationStatus.COMPLETED
            );
            Reservation savedReservation = reservationRepository.save(reservation);
            paymentService.createPaymentForReservation(savedReservation, PaymentType.RESERVATION,newReservationDto.getReservationPrice());
            if (newReservationDto.getInsurancePrice().compareTo(BigDecimal.ZERO) > 0) {
                paymentService.createPaymentForReservation(savedReservation, PaymentType.INSURANCE, newReservationDto.getInsurancePrice());
            }
            return ResponseEntity.ok(savedReservation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating the reservation: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Transactional
    public ResponseEntity<?> cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation not found");
        }
        Reservation reservation = reservationOpt.get();
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return ResponseEntity.badRequest().body("Reservation is already canceled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation canceled successfully");
    }

    private boolean isVehicleAvailable(Long vehicleId, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate) {
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(vehicleId, requestedStartDate, requestedEndDate);
        return overlappingReservations.isEmpty();
    }
}
