package dev.esteban.msrental.service;

import dev.esteban.msrental.dto.PriceDto;
import dev.esteban.msrental.dto.VehiclePriceDto;
import dev.esteban.msrental.enums.PaymentType;
import dev.esteban.msrental.enums.RentalStatus;
import dev.esteban.msrental.enums.ReservationStatus;
import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Rental;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.RentalRepository;
import dev.esteban.msrental.repository.ReservationRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import dev.esteban.msrental.service.client.MsPricingFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {
    // Implement the logic for rental management here
    // This could include methods for creating, updating, deleting, and retrieving rentals
    // For example:
    // public Rental createRental(Rental rental) { ... }
    // public Rental updateRental(Long id, Rental rental) { ... }
    // public void deleteRental(Long id) { ... }
    // public List<Rental> getAllRentals() { ... }
    // public Rental getRentalById(Long id) { ... }

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MsPricingFeignClient msPricingFeignClient;

    @Transactional
    public Rental createRentalFromReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new RuntimeException("Reservation is not completed");
        }

        Vehicle vehicle = reservation.getVehicle();

        if (vehicle.getStatus() != StatusVehicle.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available");
        }

        Rental rental = new Rental();
        rental.setReservation(reservation);
        rental.setUserId(reservation.getUserId());
        rental.setVehicle(vehicle);
        rental.setStore(vehicle.getStore());
        rental.setStartDate(reservation.getRequested_start_date());
        rental.setEndDate(reservation.getRequested_end_date());
        rental.setStatus(RentalStatus.IN_PROGRESS);
        rental.setTotalPrice(calculateRentalPrice(vehicle, reservation.getRequested_start_date(), reservation.getRequested_end_date()));
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());

        vehicle.setStatus(StatusVehicle.RENTED);
        vehicleRepository.save(vehicle);

        paymentService.createPayment(rental, PaymentType.RENTAL,rental.getTotalPrice());

        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental completeRental(Long rentalId) {
        Rental rental = getRentalById(rentalId);

        if (rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RuntimeException("Rental is not in progress");
        }

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setUpdatedAt(LocalDateTime.now());

        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(StatusVehicle.AVAILABLE);
        vehicleRepository.save(vehicle);

        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental returnVehicle(Long rentalId) {
        Rental rental = getRentalById(rentalId);

        if (rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RuntimeException("Rental is not in progress");
        }

        rental.setStatus(RentalStatus.RETURNED);
        rental.setUpdatedAt(LocalDateTime.now());

        if (LocalDateTime.now().isAfter(rental.getEndDate())) {
            paymentService.createPayment(rental, PaymentType.FINE, calculateLateFee(rental)); // 10% late fee
        }

        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(StatusVehicle.AVAILABLE);
        vehicleRepository.save(vehicle);

        paymentService.createPayment(rental, PaymentType.DEPOSIT_RETURN, calculateDepositReturn(rental));

        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental cancelRental(Long rentalId) {
        Rental rental = getRentalById(rentalId);

        if (rental.getStatus() == RentalStatus.COMPLETED || rental.getStatus() == RentalStatus.RETURNED) {
            throw new RuntimeException("Cannot cancel a completed or returned rental");
        }

        rental.setStatus(RentalStatus.CANCELED);
        rental.setUpdatedAt(LocalDateTime.now());

        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(StatusVehicle.AVAILABLE);
        vehicleRepository.save(vehicle);

        paymentService.createPayment(rental, PaymentType.REFUND, calculateRefundAmount(rental));

        return rentalRepository.save(rental);
    }

    public void checkOverdueRentals() {
        List<Rental> inProgressRentals = rentalRepository.findByStatus(RentalStatus.IN_PROGRESS);
        LocalDateTime now = LocalDateTime.now();

        for (Rental rental : inProgressRentals) {
            if (now.isAfter(rental.getEndDate())) {
                rental.setStatus(RentalStatus.OVERDUE);
                rental.setUpdatedAt(now);
                rentalRepository.save(rental);

                paymentService.createPayment(rental, PaymentType.FINE, calculateOverdueFee(rental));            }
        }
    }

    public Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByUserId(Integer userId) {
        return rentalRepository.findByUserId(userId);
    }

    public List<Rental> getRentalsByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status);
    }

    private BigDecimal calculateLateFee(Rental rental) {
        BigDecimal lateFeePercentage = new BigDecimal("0.10"); // 10% late fee
        return rental.getTotalPrice().multiply(lateFeePercentage);
    }

    private BigDecimal calculateDepositReturn(Rental rental) {
        BigDecimal depositPercentage = new BigDecimal("0.20"); // 20% deposit
        return rental.getTotalPrice().multiply(depositPercentage);
    }

    private BigDecimal calculateRefundAmount(Rental rental) {
        BigDecimal refundPercentage = new BigDecimal("0.80"); // 80% refund
        return rental.getTotalPrice().multiply(refundPercentage);
    }

    private BigDecimal calculateOverdueFee(Rental rental) {
        BigDecimal overdueFeePercentage = new BigDecimal("0.15"); // 15% overdue fee
        return rental.getTotalPrice().multiply(overdueFeePercentage);
    }

    private BigDecimal calculateRentalPrice(Vehicle vehicle, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate) {
        VehiclePriceDto vehiclePriceDto = new VehiclePriceDto(
                vehicle.getId(),
                vehicle.getCategory().getName(),
                vehicle.getPricePerDay(),
                requestedStartDate,
                requestedEndDate
        );

        ResponseEntity<?> response = msPricingFeignClient.getPricesByCar(vehiclePriceDto);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return ((PriceDto) response.getBody()).getRealRentalPrice();
        } else {
            throw new RuntimeException("Failed to retrieve pricing information");
        }
    }
}
