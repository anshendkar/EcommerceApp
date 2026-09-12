package com.ecom.gofitEcommerce.controller.admin;

import com.ecom.gofitEcommerce.DTO.CategoryDto;
import com.ecom.gofitEcommerce.entity.Category;
import com.ecom.gofitEcommerce.service.admin.CategoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryService.createcategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categoryDtos = categoryService.fetchAllCategories();
        return ResponseEntity.ok(categoryDtos);
    }
}
