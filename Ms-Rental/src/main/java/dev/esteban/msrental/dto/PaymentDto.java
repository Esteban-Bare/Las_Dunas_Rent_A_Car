package dev.esteban.msrental.dto;

import dev.esteban.msrental.enums.PaymentStatus;
import dev.esteban.msrental.enums.PaymentType;
import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.model.Rental;
import dev.esteban.msrental.model.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private Integer userId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private String description;
    private ReservationDto reservation;
    private RentalDto rental;

    public PaymentDto(Payment payment) {
        this.id = payment.getId();
        this.userId = payment.getUserId();
        this.amount = payment.getAmount();
        this.paymentDate = payment.getPaymentDate();
        this.paymentType = payment.getPaymentType();
        this.paymentStatus = payment.getPaymentStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.transactionId = payment.getTransactionId();
        this.description = payment.getDescription();
        if (payment.getReservation() != null) {
            this.reservation = new ReservationDto(payment.getReservation());
        }
        if (payment.getRental() != null) {
            this.rental = new RentalDto(payment.getRental());
        }
    }
}
