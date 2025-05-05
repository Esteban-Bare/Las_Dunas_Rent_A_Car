package dev.esteban.msrental.repository;

import dev.esteban.msrental.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByName(String name);

    @Query("SELECT DISTINCT s FROM Store s LEFT JOIN FETCH s.vehicles")
    List<Store> findAllWithVehicles();
}
