package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private BigDecimal reservationPrice;
    private BigDecimal insuranceRefundPrice;
    private BigDecimal realRentalPrice;
}
