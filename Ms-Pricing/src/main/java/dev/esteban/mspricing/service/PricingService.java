package dev.esteban.mspricing.service;

import dev.esteban.mspricing.dto.PriceDto;
import dev.esteban.mspricing.dto.VehiclePriceDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class PricingService {


    public PriceDto getPricesByCar(VehiclePriceDto vehicle) {
        if (vehicle.getRentalDateStart() == null || vehicle.getRentalDateEnd() == null) {
            throw new IllegalArgumentException("Rental start and end dates cannot be null");
        }

        long days = ChronoUnit.DAYS.between(vehicle.getRentalDateStart().toLocalDate(),
                vehicle.getRentalDateEnd().toLocalDate());

        days = Math.max(1, days);

        Map<String, BigDecimal> categoryAdjustments = Map.of(
                "SUV", new BigDecimal("0.01"),
                "Sedan", new BigDecimal("0.03"),
                "Truck", new BigDecimal("0.03")
        );

        BigDecimal adjustmentRate = categoryAdjustments.getOrDefault(
                vehicle.getCategory(), BigDecimal.ZERO);

        BigDecimal adjustedDailyPrice = vehicle.getPricePerDay()
                .multiply(BigDecimal.ONE.add(adjustmentRate));

        BigDecimal baseRentalPrice = adjustedDailyPrice.multiply(BigDecimal.valueOf(days));

        BigDecimal reservationPrice = vehicle.getPricePerDay()
                .multiply(new BigDecimal("0.20"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal insuranceRefundPrice = reservationPrice
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);

        return new PriceDto(
                reservationPrice,
                insuranceRefundPrice,
                baseRentalPrice.setScale(2, RoundingMode.HALF_UP)
        );
    }
}
