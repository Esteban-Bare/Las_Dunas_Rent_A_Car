package dev.esteban.msrental.controller;

import dev.esteban.msrental.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rental/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/names")
    public List<String> getAllCategoriesNames() {
        return categoryService.getAllCategoriesNames();
    }
}
