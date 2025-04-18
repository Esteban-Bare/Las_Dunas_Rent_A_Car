package dev.esteban.mspromo.controller;

import dev.esteban.mspromo.model.Promotion;
import dev.esteban.mspromo.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PromotionController {
    @Autowired
    private PromotionRepository promotionRepository;

    @PostMapping("/promotions")
    public List<Promotion> getPromotions() {
        return promotionRepository.findAll();
    }

    @PostMapping("/promotions/{vehicleId}")
    public List<Promotion> getPromotionsByVehicleId(@PathVariable String vehicleId) {
        return promotionRepository.findPromotionsByVehicleId(vehicleId);
    }
}
