package dev.esteban.msrental.repository;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clear all data
        vehicleRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
        storeRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    void testFindByCategory() {
        // Create dependencies
        Brand toyotaBrand = createAndPersistBrand("Toyota");
        Brand bmwBrand = createAndPersistBrand("BMW");

        Category economyCategory = createAndPersistCategory("Economy");
        Category luxuryCategory = createAndPersistCategory("Luxury");

        Store store = createAndPersistStore("Test Store");

        // Create vehicles
        Vehicle economyVehicle = new Vehicle("Corolla", "TOY-001",
                BigDecimal.valueOf(45.99), StatusVehicle.AVAILABLE,
                toyotaBrand, economyCategory, store);

        Vehicle luxuryVehicle = new Vehicle("X5", "BMW-001",
                BigDecimal.valueOf(120.00), StatusVehicle.AVAILABLE,
                bmwBrand, luxuryCategory, store);

        vehicleRepository.save(economyVehicle);
        vehicleRepository.save(luxuryVehicle);
        entityManager.flush();

        // When - search by exact category name
        List<Vehicle> economyVehicles = vehicleRepository.findByCategory("Economy");

        // Then
        assertThat(economyVehicles).hasSize(1);
        assertThat(economyVehicles.get(0).getBrand().getName()).isEqualTo("Toyota");
        assertThat(economyVehicles.get(0).getModel()).isEqualTo("Corolla");
    }

    @Test
    void testFindAvailableVehicles() {
        // Create dependencies
        Brand hondaBrand = createAndPersistBrand("Honda");
        Brand fordBrand = createAndPersistBrand("Ford");

        Category compactCategory = createAndPersistCategory("Compact");
        Store store = createAndPersistStore("Test Store 2");

        // Create vehicles
        Vehicle available = new Vehicle("Civic", "HON-001",
                BigDecimal.valueOf(40.00), StatusVehicle.AVAILABLE,
                hondaBrand, compactCategory, store);

        Vehicle unavailable = new Vehicle("Focus", "FOR-001",
                BigDecimal.valueOf(42.00), StatusVehicle.RENTED,
                fordBrand, compactCategory, store);

        vehicleRepository.save(available);
        vehicleRepository.save(unavailable);
        entityManager.flush();

        // When
        List<Vehicle> availableVehicles = vehicleRepository.findByAvailableTrue();

        // Then
        assertThat(availableVehicles).hasSize(1);
        assertThat(availableVehicles.get(0).getBrand().getName()).isEqualTo("Honda");
        assertThat(availableVehicles.get(0).getModel()).isEqualTo("Civic");
        assertThat(availableVehicles.get(0).getStatus()).isEqualTo(StatusVehicle.AVAILABLE);
    }

    private Brand createAndPersistBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return entityManager.persistAndFlush(brand);
    }

    private Category createAndPersistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return entityManager.persistAndFlush(category);
    }

    private Store createAndPersistStore(String name) {
        Store store = new Store();
        store.setName(name);
        store.setAddress("123 Test St");
        store.setCity("Test City");
        store.setPhone("555-0000");
        return entityManager.persistAndFlush(store);
    }
}