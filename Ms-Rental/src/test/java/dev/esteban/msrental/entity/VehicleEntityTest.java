package dev.esteban.msrental.entity;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Transactional
@Rollback
class VehicleEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testVehicleCreation() {
        Brand brand = new Brand();
        brand.setName("Toyota-" + System.currentTimeMillis()); // Make unique
        entityManager.persist(brand);

        Category category = new Category();
        category.setName("Economy-" + System.currentTimeMillis()); // Make unique
        entityManager.persist(category);

        Store store = new Store();
        store.setName("Main Street Store");
        store.setAddress("123 Main St, Cityville");
        store.setCity("Main Street");
        store.setPhone("555-1234");
        entityManager.persist(store);

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel("Corolla");
        vehicle.setCategory(category);
        vehicle.setStore(store);
        vehicle.setPlateNumber("ABC-1234");
        vehicle.setPricePerDay(BigDecimal.valueOf(45.99));
        vehicle.setStatus(StatusVehicle.AVAILABLE);

        Vehicle saved = entityManager.persistAndFlush(vehicle);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBrand().getName()).startsWith("Toyota-");
        assertThat(saved.getModel()).isEqualTo("Corolla");
        assertThat(saved.getPricePerDay()).isEqualByComparingTo(BigDecimal.valueOf(45.99));
    }
}