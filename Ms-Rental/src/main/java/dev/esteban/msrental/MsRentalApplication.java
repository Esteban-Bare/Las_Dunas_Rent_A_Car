package dev.esteban.msrental;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.BrandRepository;
import dev.esteban.msrental.repository.CategoryRepository;
import dev.esteban.msrental.repository.StoreRepository;
import dev.esteban.msrental.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class MsRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsRentalApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner commandLineRunner(BrandRepository brandRepository, CategoryRepository categoryRepository, VehicleRepository vehicleRepository, StoreRepository storeRepository) {
        return args -> {
            // Initialize brands
            brandRepository.save(new Brand("Toyota"));
            brandRepository.save(new Brand("Honda"));
            brandRepository.save(new Brand("Ford"));
            brandRepository.save(new Brand("Volkswagen"));
            brandRepository.save(new Brand("BMW"));
            brandRepository.save(new Brand("Mercedes"));
            brandRepository.save(new Brand("Audi"));
            brandRepository.save(new Brand("Peugeot"));
            brandRepository.save(new Brand("Renault"));

            // Initialize categories
            categoryRepository.save(new Category("SUV"));
            categoryRepository.save(new Category("Sedan"));
            categoryRepository.save(new Category("Truck"));
            categoryRepository.save(new Category("Compact"));
            categoryRepository.save(new Category("Luxury"));
            categoryRepository.save(new Category("Electric"));

            // Initialize stores
            storeRepository.save(new Store("Toulon Centre", "1 Place de la Liberte 83000", "0123456789", "toulon"));
            storeRepository.save(new Store("Toulon Port", "2 Rue de la Republique 83000", "0987654321", "toulon"));
            storeRepository.save(new Store("Marseille Gare Saint Charles", "Sq. Narvik, 13232 Marseille", "0147852369", "marseille"));

            // Get references to avoid repeated queries
            Brand toyota = brandRepository.findByName("Toyota").orElse(null);
            Brand honda = brandRepository.findByName("Honda").orElse(null);
            Brand ford = brandRepository.findByName("Ford").orElse(null);
            Brand volkswagen = brandRepository.findByName("Volkswagen").orElse(null);
            Brand bmw = brandRepository.findByName("BMW").orElse(null);
            Brand mercedes = brandRepository.findByName("Mercedes").orElse(null);
            Brand audi = brandRepository.findByName("Audi").orElse(null);
            Brand peugeot = brandRepository.findByName("Peugeot").orElse(null);
            Brand renault = brandRepository.findByName("Renault").orElse(null);

            Category suv = categoryRepository.findByName("SUV").orElse(null);
            Category sedan = categoryRepository.findByName("Sedan").orElse(null);
            Category truck = categoryRepository.findByName("Truck").orElse(null);
            Category compact = categoryRepository.findByName("Compact").orElse(null);
            Category luxury = categoryRepository.findByName("Luxury").orElse(null);
            Category electric = categoryRepository.findByName("Electric").orElse(null);

            Store toulonCentre = storeRepository.findByName("Toulon Centre").orElse(null);
            Store toulonPort = storeRepository.findByName("Toulon Port").orElse(null);
            Store marseille = storeRepository.findByName("Marseille Gare Saint Charles").orElse(null);

            // Add more vehicles - Toulon Centre
            vehicleRepository.save(new Vehicle("RAV4", "ABC123", new BigDecimal("50.00"), StatusVehicle.AVAILABLE, toyota, suv, toulonCentre));
            vehicleRepository.save(new Vehicle("Highlander", "TOY001", new BigDecimal("65.00"), StatusVehicle.AVAILABLE, toyota, suv, toulonCentre));
            vehicleRepository.save(new Vehicle("Camry", "TOY002", new BigDecimal("45.00"), StatusVehicle.AVAILABLE, toyota, sedan, toulonCentre));
            vehicleRepository.save(new Vehicle("Corolla", "TOY003", new BigDecimal("35.00"), StatusVehicle.AVAILABLE, toyota, compact, toulonCentre));
            vehicleRepository.save(new Vehicle("Civic", "XYZ789", new BigDecimal("40.00"), StatusVehicle.AVAILABLE, honda, sedan, toulonCentre));
            vehicleRepository.save(new Vehicle("CR-V", "HON001", new BigDecimal("55.00"), StatusVehicle.AVAILABLE, honda, suv, toulonCentre));
            vehicleRepository.save(new Vehicle("Accord", "HON002", new BigDecimal("48.00"), StatusVehicle.AVAILABLE, honda, sedan, toulonCentre));
            vehicleRepository.save(new Vehicle("Golf", "VW001", new BigDecimal("42.00"), StatusVehicle.AVAILABLE, volkswagen, compact, toulonCentre));

            // Toulon Port
            vehicleRepository.save(new Vehicle("F-150", "LMN456", new BigDecimal("60.00"), StatusVehicle.AVAILABLE, ford, truck, toulonPort));
            vehicleRepository.save(new Vehicle("Mustang", "FOR001", new BigDecimal("70.00"), StatusVehicle.AVAILABLE, ford, luxury, toulonPort));
            vehicleRepository.save(new Vehicle("Explorer", "FOR002", new BigDecimal("58.00"), StatusVehicle.AVAILABLE, ford, suv, toulonPort));
            vehicleRepository.save(new Vehicle("Focus", "FOR003", new BigDecimal("38.00"), StatusVehicle.AVAILABLE, ford, compact, toulonPort));
            vehicleRepository.save(new Vehicle("X3", "BMW001", new BigDecimal("75.00"), StatusVehicle.AVAILABLE, bmw, suv, toulonPort));
            vehicleRepository.save(new Vehicle("320i", "BMW002", new BigDecimal("68.00"), StatusVehicle.AVAILABLE, bmw, sedan, toulonPort));
            vehicleRepository.save(new Vehicle("C-Class", "MER001", new BigDecimal("72.00"), StatusVehicle.AVAILABLE, mercedes, luxury, toulonPort));
            vehicleRepository.save(new Vehicle("GLA", "MER002", new BigDecimal("70.00"), StatusVehicle.AVAILABLE, mercedes, suv, toulonPort));

            // Marseille
            vehicleRepository.save(new Vehicle("Ka", "ABC456", new BigDecimal("30.00"), StatusVehicle.AVAILABLE, ford, compact, marseille));
            vehicleRepository.save(new Vehicle("A4", "AUD001", new BigDecimal("65.00"), StatusVehicle.AVAILABLE, audi, sedan, marseille));
            vehicleRepository.save(new Vehicle("Q5", "AUD002", new BigDecimal("78.00"), StatusVehicle.AVAILABLE, audi, suv, marseille));
            vehicleRepository.save(new Vehicle("208", "PEU001", new BigDecimal("32.00"), StatusVehicle.AVAILABLE, peugeot, compact, marseille));
            vehicleRepository.save(new Vehicle("3008", "PEU002", new BigDecimal("48.00"), StatusVehicle.AVAILABLE, peugeot, suv, marseille));
            vehicleRepository.save(new Vehicle("5008", "PEU003", new BigDecimal("52.00"), StatusVehicle.AVAILABLE, peugeot, suv, marseille));
            vehicleRepository.save(new Vehicle("Clio", "REN001", new BigDecimal("35.00"), StatusVehicle.AVAILABLE, renault, compact, marseille));
            vehicleRepository.save(new Vehicle("Kadjar", "REN002", new BigDecimal("46.00"), StatusVehicle.AVAILABLE, renault, suv, marseille));
            vehicleRepository.save(new Vehicle("Zoe", "REN003", new BigDecimal("40.00"), StatusVehicle.AVAILABLE, renault, electric, marseille));
            vehicleRepository.save(new Vehicle("Clio", "REN004", new BigDecimal("35.00"), StatusVehicle.AVAILABLE, renault, compact, marseille));
            vehicleRepository.save(new Vehicle("Clio", "REN005", new BigDecimal("37.00"), StatusVehicle.AVAILABLE, renault, compact, toulonCentre));
            vehicleRepository.save(new Vehicle("Tiguan", "VW002", new BigDecimal("56.00"), StatusVehicle.AVAILABLE, volkswagen, suv, marseille));
        };
    }
}
