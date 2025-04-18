package dev.esteban.mspromo;

import dev.esteban.mspromo.model.Promotion;
import dev.esteban.mspromo.repository.PromotionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class MsPromoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPromoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(PromotionRepository promotionRepository) {
        return args -> {
             promotionRepository.deleteAll();
             Promotion promo1 = new Promotion("1", "Promo 1", "Description 1", LocalDate.now().toString(), LocalDate.now().plusMonths(6).toString(), 10);
             Promotion promo2 = new Promotion("2", "Promo 2", "Description 2", LocalDate.now().toString(), LocalDate.now().plusMonths(6).toString(), 20);
             Promotion promo2v2 = new Promotion("2", "Weekend Discount", "Last Chance Discount", LocalDate.now().toString(), LocalDate.now().plusDays(3).toString(), 30);
             Promotion promo3 = new Promotion("3", "Promo 3", "Description 3", LocalDate.now().toString(), LocalDate.now().plusMonths(6).toString(), 30);
             promotionRepository.save(promo1);
             promotionRepository.save(promo2);
             promotionRepository.save(promo2v2);
             promotionRepository.save(promo3);
        };
    }
}
