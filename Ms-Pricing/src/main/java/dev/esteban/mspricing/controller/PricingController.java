package dev.esteban.mspricing.controller;

import dev.esteban.mspricing.dto.PriceDto;
import dev.esteban.mspricing.dto.VehiclePriceDto;
import dev.esteban.mspricing.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PricingController {

    @Autowired
    private PricingService pricingService;

    @GetMapping("/pricing")
    private ResponseEntity<PriceDto> getPricesByCar(@RequestBody VehiclePriceDto vehicle) {
        PriceDto priceDto = pricingService.getPricesByCar(vehicle);
        if (priceDto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(priceDto);
    }
}
