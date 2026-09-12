package com.ecom.gofitEcommerce.service.admin;


import com.ecom.gofitEcommerce.DTO.ProductDto;
import com.ecom.gofitEcommerce.DTO.ProductPageResponse;
import com.ecom.gofitEcommerce.entity.Category;
import com.ecom.gofitEcommerce.entity.Product;
import com.ecom.gofitEcommerce.repository.CategoryRepository;
import com.ecom.gofitEcommerce.repository.ProductRepository;
import com.ecom.gofitEcommerce.utils.Mapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    @Value("${app.upload.product-dir:uploads}")
    private String uploadDir;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    @Caching(
            put = { @CachePut(value = "product", key = "#result.id") },
            evict = { @CacheEvict(value = "products", allEntries = true) }
    )
    public ProductDto addProduct(ProductDto dto) throws IOException {
        MultipartFile imgFile = dto.getImg();
        String imgUrl = null;
        if (imgFile != null && !imgFile.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + imgFile.getOriginalFilename();
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(target.getParent());
            Files.copy(imgFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            imgUrl = "/uploads/" + filename;
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setImgUrl(imgUrl);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return Mapper.toDto(saved);
    }

    // Cache all products page - note: Pageable caching is tricky, cache by page number
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public ProductPageResponse getProducts(Pageable pageable) {
        System.out.println("data coming from redis");
        Page<Product> page = productRepository.findAll(pageable);
        return new ProductPageResponse(
                page.getContent().stream().map(Mapper::toDto).toList(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements());
    }

    @Cacheable(value = "products", key = "#name")
    @Transactional
    public List<ProductDto> getAllProductsByname(String name) {
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Mapper::toDto).collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "products", allEntries = true)  // clear all paginated & search cache
    })
    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalproduct = productRepository.findById(id);
        if (optionalproduct.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Cacheable(value = "product", key = "#productId")
    @Transactional
    public ProductDto getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.map(Mapper::toDto).orElse(null);
    }

    @Caching(
            put = { @CachePut(value = "product", key = "#productId") },
            evict = { @CacheEvict(value = "products", allEntries = true) }
    )
    @Transactional
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());

        if (optionalProduct.isPresent() && optionalCategory.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCategory(optionalCategory.get());

            if (productDto.getImgUrl() != null) {
                product.setImgUrl(productDto.getImgUrl());
            }

            Product updatedProduct = productRepository.save(product);
            return Mapper.toDto(updatedProduct);
        } else {
            return null;
        }
    }


}
