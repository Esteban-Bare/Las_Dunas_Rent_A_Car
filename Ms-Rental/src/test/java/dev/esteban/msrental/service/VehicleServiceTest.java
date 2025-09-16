package dev.esteban.msrental.service;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void testVehicleIsAvailable() {
        // Given
        Brand toyota = new Brand("Toyota");
        Category sedan = new Category("Sedan");
        Store store = new Store("Test Store", "Test Address", "123456789", "testcity");

        Vehicle availableVehicle = new Vehicle("Camry", "TOY001",
                BigDecimal.valueOf(65.00), StatusVehicle.AVAILABLE, toyota, sedan, store);

        // When
        boolean isAvailable = vehicleService.vehicleIsAvailable(availableVehicle);

        // Then
        assertThat(isAvailable).isTrue();
    }

    @Test
    void testVehicleIsNotAvailable() {
        // Given
        Brand honda = new Brand("Honda");
        Category sedan = new Category("Sedan");
        Store store = new Store("Test Store", "Test Address", "123456789", "testcity");

        Vehicle unavailableVehicle = new Vehicle("Accord", "HON001",
                BigDecimal.valueOf(62.00), StatusVehicle.RENTED, honda, sedan, store);

        // When
        boolean isAvailable = vehicleService.vehicleIsAvailable(unavailableVehicle);

        // Then
        assertThat(isAvailable).isFalse();
    }
}