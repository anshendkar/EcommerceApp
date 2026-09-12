package com.ecom.gofitEcommerce.service.admin;

import com.ecom.gofitEcommerce.DTO.CategoryDto;
import com.ecom.gofitEcommerce.entity.Category;
import com.ecom.gofitEcommerce.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createcategory(CategoryDto categoryDto){
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryRepository.save(category);
    }

//    public Page<CategoryDto> getAllCategories(int page, int size, String sortBy, String sortDir) {
//        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        return categoryRepository.findAll(pageable).map(this::mapToDto);
//    }
//
//    private CategoryDto mapToDto(Category category) {
//        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
//    }

    @Transactional
    public List<CategoryDto> fetchAllCategories() {
        List<Category> categories = categoryRepository.findAll(Sort.by("name").ascending());
        return categories.stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName(), cat.getDescription()))
                .toList();
    }
}
