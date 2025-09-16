package dev.esteban.msrental.dto;

import dev.esteban.msrental.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private Integer userId;
    private VehicleReservationDto vehicle;
    private StoreDto store;
    private LocalDateTime requested_start_date;
    private LocalDateTime requested_end_date;
    private String status;
    private String reservationPrice;
    private String insuranceRefundPrice;
    private LocalDateTime createdAt;
    private List<PaymentWithoutReservationAndRentalDto> payments;
    private boolean hasRental;

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUserId();
        this.vehicle = new VehicleReservationDto(reservation.getVehicle());
        this.store = new StoreDto(reservation.getStore());
        this.requested_start_date = reservation.getRequested_start_date();
        this.requested_end_date = reservation.getRequested_end_date();
        this.status = reservation.getStatus().name();
        this.reservationPrice = reservation.getReservationPrice().toString();
        this.insuranceRefundPrice = reservation.getInsuranceRefundPrice().toString();
        if (reservation.getPayments() != null) {
            this.payments = reservation.getPayments().stream().map(PaymentWithoutReservationAndRentalDto::new).collect(Collectors.toList());
        }
        this.createdAt = reservation.getCreatedAt();
        if (reservation.getRentals() != null && !reservation.getRentals().isEmpty()) {
            this.hasRental = true;
        } else {
            this.hasRental = false;
        }
    }
}
