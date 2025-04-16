package dev.esteban.msrental;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.BrandRepository;
import dev.esteban.msrental.repository.CategoryRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@EnableDiscoveryClient
public class MsRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsRentalApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BrandRepository brandRepository, CategoryRepository categoryRepository, VehicleRepository vehicleRepository) {
        return args -> {
            // Initialize the database with some data
            brandRepository.save(new Brand("Toyota"));
            brandRepository.save(new Brand("Honda"));
            brandRepository.save(new Brand("Ford"));

            categoryRepository.save(new Category("SUV"));
            categoryRepository.save(new Category("Sedan"));
            categoryRepository.save(new Category("Truck"));

            vehicleRepository.save(new Vehicle("RAV4", "ABC123", new BigDecimal("50.00"), StatusVehicle.AVAILABLE ,brandRepository.findByName("Toyota").orElse(null), categoryRepository.findByName("SUV").orElse(null)));
            vehicleRepository.save(new Vehicle("Civic", "XYZ789", new BigDecimal("40.00"),StatusVehicle.AVAILABLE ,brandRepository.findByName("Honda").orElse(null), categoryRepository.findByName("Sedan").orElse(null)));
            vehicleRepository.save(new Vehicle("F-150", "LMN456", new BigDecimal("60.00"),StatusVehicle.AVAILABLE ,brandRepository.findByName("Ford").orElse(null), categoryRepository.findByName("Truck").orElse(null)));
        };
    }
}
