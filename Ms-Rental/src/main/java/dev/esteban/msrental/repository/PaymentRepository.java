package dev.esteban.msrental.repository;

import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.model.Rental;
import dev.esteban.msrental.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Integer userId);

    List<Payment> findByRental(Rental rental);

    List<Payment> findByReservation(Reservation reservation);
}
