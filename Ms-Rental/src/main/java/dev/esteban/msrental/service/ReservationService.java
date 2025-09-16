package dev.esteban.msrental.service;

import dev.esteban.msrental.dto.AllReservationsAdminDto;
import dev.esteban.msrental.dto.NewReservationDto;
import dev.esteban.msrental.dto.ReservationDto;
import dev.esteban.msrental.dto.UserDto;
import dev.esteban.msrental.enums.PaymentStatus;
import dev.esteban.msrental.enums.PaymentType;
import dev.esteban.msrental.enums.ReservationStatus;
import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.ReservationRepository;
import dev.esteban.msrental.repository.StoreRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.client.MsSecurityFeignClient;
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
    @Autowired
    private MsSecurityFeignClient msSecurityFeignClient;

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
            LocalDateTime now = LocalDateTime.now();
            if (newReservationDto.getRequestedStartDate().isBefore(now) || newReservationDto.getRequestedEndDate().isBefore(now) ||
                    newReservationDto.getRequestedEndDate().isBefore(newReservationDto.getRequestedStartDate())) {
                return ResponseEntity.badRequest().body("Invalid reservation dates");
            }

            Reservation reservation = new Reservation(newReservationDto.getUserId(), vehicle.get(), store.get(), newReservationDto.getRequestedStartDate(),
                    newReservationDto.getRequestedEndDate(), newReservationDto.getReservationPrice(), newReservationDto.getInsurancePrice(), ReservationStatus.COMPLETED
            );
            Reservation savedReservation = reservationRepository.save(reservation);

            paymentService.createPaymentForReservation(savedReservation, PaymentType.RESERVATION,newReservationDto.getReservationPrice());
            if (newReservationDto.getInsurancePrice().compareTo(BigDecimal.ZERO) > 0) {
                paymentService.createPaymentForReservation(savedReservation, PaymentType.INSURANCE, newReservationDto.getInsurancePrice());
            }
            ReservationDto reservationDto = new ReservationDto(reservation);
            return ResponseEntity.ok(reservationDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating the reservation: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId).stream().map(ReservationDto::new).toList();
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

        List<Payment> Payments = paymentService.getPaymentsByReservation(reservation);

        for (Payment payment : Payments) {
            if (reservation.getInsuranceRefundPrice().compareTo(BigDecimal.ZERO) > 0 && payment.getPaymentStatus() == PaymentStatus.COMPLETED && payment.getPaymentType() == PaymentType.RESERVATION) {
                paymentService.refundPayment(payment.getId(), reservation.getReservationPrice());
            } else if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
                paymentService.cancelPayment(payment.getId());
            }
        }

        System.out.println("Reservation with ID " + reservationId + " has been canceled.");
        return ResponseEntity.ok("Reservation canceled successfully");
    }

    private boolean isVehicleAvailable(Long vehicleId, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate) {
        int bufferHours = 12; // 12-hour buffer before and after the reservation
        LocalDateTime bufferedStartDate = requestedStartDate.minusHours(bufferHours);
        LocalDateTime bufferedEndDate = requestedEndDate.plusHours(bufferHours);

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                vehicleId,
                bufferedStartDate,
                bufferedEndDate
        );
        return overlappingReservations.isEmpty();
    }

    public List<AllReservationsAdminDto> getAllReservationsAdmin() {
        return reservationRepository.findAll().stream().map(reservation -> {
            List<PaymentStatus> paymentStatuses = reservation.getPayments().stream()
                    .map(Payment::getPaymentStatus)
                    .toList();
            boolean hasRental = reservation.getRentals() != null && !reservation.getRentals().isEmpty();
            UserDto userDto = msSecurityFeignClient.getUserById(Long.valueOf(reservation.getUserId())).getBody();
            return new AllReservationsAdminDto(
                    reservation.getId(),
                    userDto != null ? userDto.getFirstName() : "Unknown",
                    userDto != null ? userDto.getLastName() : "",
                    reservation.getVehicle().getModel(),
                    reservation.getVehicle().getPlateNumber(),
                    paymentStatuses,
                    reservation.getRequested_start_date(),
                    reservation.getRequested_end_date(),
                    reservation.getStatus(),
                    reservation.getReservationPrice().add(reservation.getInsuranceRefundPrice()),
                    hasRental
            );
        }).toList();
    }
}
