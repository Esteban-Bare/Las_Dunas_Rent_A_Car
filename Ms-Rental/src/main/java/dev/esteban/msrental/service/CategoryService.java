package dev.esteban.msrental.service;

import dev.esteban.msrental.model.Category;
import dev.esteban.msrental.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<String> getAllCategoriesNames() {
        return categoryRepository.findAll().stream().map(Category::getName).toList();
    }
}
