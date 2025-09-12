package dev.esteban.msrental.repository;

import dev.esteban.msrental.enums.RentalStatus;
import dev.esteban.msrental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStatus(RentalStatus rentalStatus);

    List<Rental> findByUserId(Integer userId);
}
