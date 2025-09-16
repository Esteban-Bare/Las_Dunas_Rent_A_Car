package dev.esteban.msrental.service;

import dev.esteban.msrental.model.Brand;
import dev.esteban.msrental.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<String> getAllBrandNames() {
        return brandRepository.findAll().stream().map(Brand::getName).toList();
    }
}
