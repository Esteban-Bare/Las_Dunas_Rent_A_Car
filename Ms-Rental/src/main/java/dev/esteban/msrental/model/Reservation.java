package dev.esteban.msrental.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.esteban.msrental.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "reservation")
    private Set<Payment> payments;

    @Column(nullable = false)
    private LocalDateTime requested_start_date;

    @Column(nullable = false)
    private LocalDateTime requested_end_date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    private BigDecimal reservationPrice;

    @Column(nullable = false)
    private BigDecimal insuranceRefundPrice;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Set<Rental> rentals;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Reservation(Integer userId, Vehicle vehicle, Store store, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate, BigDecimal reservationPrice, BigDecimal insurancePrice, ReservationStatus reservationStatus) {
        this.userId = userId;
        this.vehicle = vehicle;
        this.store = store;
        this.requested_start_date = requestedStartDate;
        this.requested_end_date = requestedEndDate;
        this.reservationPrice = reservationPrice;
        this.insuranceRefundPrice = insurancePrice;
        this.status = reservationStatus;
    }
}
