package dev.esteban.msrental.controller;

import dev.esteban.msrental.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rental/brands")
public class BrandController{
    @Autowired
    private BrandService brandService;

    @GetMapping("/names")
    public List<String> getAllBrandNames() {
        return brandService.getAllBrandNames();
    }
}
