package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewReservationDto {
    private Integer userId;
    private Long vehicleId;
    private Long storeId;
    private LocalDateTime requestedStartDate;
    private LocalDateTime requestedEndDate;
    private BigDecimal reservationPrice;
    private BigDecimal insurancePrice;
}
