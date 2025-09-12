package dev.esteban.msrental.service;

import dev.esteban.msrental.enums.PaymentStatus;
import dev.esteban.msrental.enums.PaymentType;
import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.model.Rental;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(Rental rental, PaymentType paymentType, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setUserId(rental.getUserId());
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType(paymentType);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod("CREDIT_CARD"); // Default payment method
        payment.setTransactionId(generateTransactionId());
        payment.setRental(rental);
        payment.setDescription(generatePaymentDescription(paymentType, rental));

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPaymentForReservation(Reservation reservation, PaymentType paymentType, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setUserId(reservation.getUserId());
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType(paymentType);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod("CREDIT_CARD"); // Default payment method
        payment.setTransactionId(generateTransactionId());
        payment.setReservation(reservation);
        payment.setDescription(generatePaymentDescriptionForReservation(paymentType, reservation));

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processPayment(Long paymentId, String paymentMethod) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Simulate payment processing
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed payment");
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByUserId(Integer userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByRental(Rental rental) {
        return paymentRepository.findByRental(rental);
    }

    public List<Payment> getPaymentsByReservation(Reservation reservation) {
        return paymentRepository.findByReservation(reservation);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePaymentDescription(PaymentType paymentType, Rental rental) {
        return switch (paymentType) {
            case RENTAL -> "Rental payment for vehicle " + rental.getVehicle().getModel();
            case FINE -> "Late return fine for rental " + rental.getId();
            case DEPOSIT_RETURN -> "Deposit return for rental " + rental.getId();
            case REFUND -> "Refund for cancelled rental " + rental.getId();
            default -> "Payment for rental " + rental.getId();
        };
    }

    private String generatePaymentDescriptionForReservation(PaymentType paymentType, Reservation reservation) {
        return switch (paymentType) {
            case RESERVATION -> "Reservation payment for vehicle " + reservation.getVehicle().getModel();
            case DEPOSIT -> "Security deposit for reservation " + reservation.getId();
            case REFUND -> "Refund for cancelled reservation " + reservation.getId();
            default -> "Payment for reservation " + reservation.getId();
        };
    }

    @Transactional
    public Payment refundPayment(Long paymentId, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot refund a non-completed payment");
        }

        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed original payment amount");
        }

        Payment refundPayment = new Payment();
        refundPayment.setUserId(payment.getUserId());
        refundPayment.setAmount(refundAmount.negate()); // Negative amount for refund
        refundPayment.setPaymentDate(LocalDateTime.now());
        refundPayment.setPaymentType(PaymentType.REFUND);
        refundPayment.setPaymentStatus(PaymentStatus.COMPLETED);
        refundPayment.setPaymentMethod(payment.getPaymentMethod());
        refundPayment.setTransactionId(generateTransactionId());
        refundPayment.setRental(payment.getRental());
        refundPayment.setReservation(payment.getReservation());
        refundPayment.setDescription("Refund for payment " + payment.getTransactionId());

        return paymentRepository.save(refundPayment);
    }

    public BigDecimal getTotalPaymentsByUser(Integer userId) {
        return paymentRepository.findByUserId(userId).stream()
                .filter(payment -> payment.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPendingPaymentsByUser(Integer userId) {
        return paymentRepository.findByUserId(userId).stream()
                .filter(payment -> payment.getPaymentStatus() == PaymentStatus.PENDING)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}