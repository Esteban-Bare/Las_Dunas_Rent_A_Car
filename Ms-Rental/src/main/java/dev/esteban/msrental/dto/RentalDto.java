package dev.esteban.msrental.dto;

import dev.esteban.msrental.model.Rental;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;
    private Integer userId;
    private VehicleDto vehicle;
    private StoreDto store;
    private ReservationDto reservation;
    private String startDate;
    private String endDate;
    private String status;
    private String totalPrice;
    private String createdAt;
    private String updatedAt;
    private PaymentWithoutReservationAndRentalDto payment;

    public RentalDto(Rental rental) {
        this.id = rental.getId();
        this.userId = rental.getUserId();
        this.vehicle = new VehicleDto(rental.getVehicle());
        this.store = new StoreDto(rental.getStore());
        if (rental.getReservation() != null) {
            this.reservation = new ReservationDto(rental.getReservation());
        }
        this.startDate = rental.getStartDate().toString();
        this.endDate = rental.getEndDate().toString();
        this.status = rental.getStatus().name();
        this.totalPrice = rental.getTotalPrice().toString();
        this.createdAt = rental.getCreatedAt().toString();
        this.updatedAt = rental.getUpdatedAt().toString();
        if (rental.getPayments() != null && !rental.getPayments().isEmpty()) {
            this.payment = new PaymentWithoutReservationAndRentalDto(rental.getPayments().iterator().next());
        }
    }
}
