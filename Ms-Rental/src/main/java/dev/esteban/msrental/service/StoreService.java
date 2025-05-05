package dev.esteban.msrental.service;

import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.model.Vehicle;
import dev.esteban.msrental.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<Store> getStoresByCity(String city) {
        String cityName = city.toLowerCase();
        // Check if city is present in any of the stores present in the database
        List<Store> all = storeRepository.findAllWithVehicles();
        if (all.stream().noneMatch(store -> store.getCity().equalsIgnoreCase(cityName))) {
            throw new RuntimeException("City not found");
        }
        // Filter the stores by city
        return all.stream()
                .filter(store -> store.getCity().equalsIgnoreCase(cityName))
                .toList();
    }
}
