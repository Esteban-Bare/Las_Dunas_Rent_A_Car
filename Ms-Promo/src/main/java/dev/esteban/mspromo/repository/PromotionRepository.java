package dev.esteban.mspromo.repository;

import dev.esteban.mspromo.model.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {
    Promotion findByPromoId(String promoId);

    void deleteByPromoId(String promoId);

    List<Promotion> findPromotionsByVehicleId(String vehicleId);
}
