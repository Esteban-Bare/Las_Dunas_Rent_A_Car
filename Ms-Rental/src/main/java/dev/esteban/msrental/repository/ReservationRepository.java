package dev.esteban.msrental.repository;

import dev.esteban.msrental.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findByUserId(Integer userId);

    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId AND " +
            "r.status = 'COMPLETED' AND " +
            "((r.requested_start_date <= :endDate AND r.requested_end_date >= :startDate))")
    List<Reservation> findOverlappingReservations(@Param("vehicleId") Long vehicleId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}
