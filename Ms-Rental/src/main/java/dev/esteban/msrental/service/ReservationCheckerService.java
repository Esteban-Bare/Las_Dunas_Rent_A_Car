package dev.esteban.msrental.service;

import dev.esteban.msrental.enums.PaymentStatus;
import dev.esteban.msrental.enums.ReservationStatus;
import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.model.Reservation;
import dev.esteban.msrental.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationCheckerService {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 43200000) // Every 12 hours
    @Transactional
    public void cancelNotPaidResevations() {
        LocalDateTime minus24 = LocalDateTime.now().minusHours(24);

        List<Reservation> oldReservations = reservationRepository.findByStatusAndCreationDateBefore(
                ReservationStatus.COMPLETED, minus24);

        for (Reservation reservation : oldReservations) {
            List<Payment> payments = paymentService.getPaymentsByReservation(reservation);

            boolean allPending = payments.stream()
                    .allMatch(payment -> payment.getPaymentStatus() == PaymentStatus.PENDING);

            if (allPending && !payments.isEmpty()) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);

                payments.forEach(payment -> {
                    try {
                        paymentService.cancelPayment(payment.getId());
                    } catch (Exception e) {
                        System.err.println("Failed to cancel payment with ID " + payment.getId() + ": " + e.getMessage());
                    }
                });

                System.out.println("Cancelled reservation with ID " + reservation.getId() + " due to non-payment.");
            }
        }
    }
}
