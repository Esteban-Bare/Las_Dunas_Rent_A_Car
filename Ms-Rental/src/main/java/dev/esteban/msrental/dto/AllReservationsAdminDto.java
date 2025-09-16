package dev.esteban.msrental.dto;

import dev.esteban.msrental.enums.PaymentStatus;
import dev.esteban.msrental.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllReservationsAdminDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String vehicleModel;
    private String vehiclePlate;
    private List<PaymentStatus> paymentStatuses;
    private LocalDateTime requestedStartDate;
    private LocalDateTime requestedEndDate;
    private ReservationStatus reservationStatus;
    private BigDecimal totalPrice;
    private boolean hasRental;
}
