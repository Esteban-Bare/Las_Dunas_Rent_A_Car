package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.CityDto;
import dev.esteban.msrental.model.Store;
import dev.esteban.msrental.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rental/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping("/stores")
    @Transactional(readOnly = true)
    public List<Store> getStoresByCity(@RequestParam String city) {
        return storeService.getStoresByCity(city);
    }

    @GetMapping("/test")
    public String test() {
        LocalDateTime now = LocalDateTime.now();
        return now.toString();
    }
}
