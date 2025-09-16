package dev.esteban.msrental.repository;

import dev.esteban.msrental.enums.StatusVehicle;
import dev.esteban.msrental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.category.name = :categoryName")
    List<Vehicle> findByCategory(@Param("categoryName") String categoryName);

    List<Vehicle> findByStatus(StatusVehicle status);

    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE'")
    List<Vehicle> findByAvailableTrue();
}