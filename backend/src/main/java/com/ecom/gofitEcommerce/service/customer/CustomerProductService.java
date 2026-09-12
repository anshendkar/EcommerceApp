package com.ecom.gofitEcommerce.service.customer;

import com.ecom.gofitEcommerce.DTO.ProductDetailDto;
import com.ecom.gofitEcommerce.DTO.ProductDto;
import com.ecom.gofitEcommerce.entity.FAQ;
import com.ecom.gofitEcommerce.entity.Product;
import com.ecom.gofitEcommerce.entity.Review;
import com.ecom.gofitEcommerce.repository.FAQRepository;
import com.ecom.gofitEcommerce.repository.ProductRepository;
import com.ecom.gofitEcommerce.repository.ReviewRepository;
import com.ecom.gofitEcommerce.utils.Mapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

    private final ProductRepository productRepository;

    private final FAQRepository faqRepository;

    private final ReviewRepository reviewRepository;


    public Page<ProductDto> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(Mapper::toDto);
    }

    @Transactional
    public List<ProductDto> getAllProductsByname(String name){
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Mapper:: toDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductDetailDto getProductDetailById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            List<FAQ> faqList = faqRepository.findAllByProductId(productId);
            List<Review> reviewList = reviewRepository.findAllByProductId(productId);

            ProductDetailDto productDetailDto = new ProductDetailDto();

            // 🔁 Use Mapper instead of .toDto()
            productDetailDto.setProductDto(Mapper.toDto(optionalProduct.get()));

            productDetailDto.setFaqDtoList(
                    faqList.stream()
                            .map(FAQ::getFAQDto)
                            .collect(Collectors.toList())
            );

            productDetailDto.setReviewDtoList(
                    reviewList.stream()
                            .map(Review::getDto)
                            .collect(Collectors.toList())
            );

            return productDetailDto;
        }

        return null; // Or throw exception if not found
    }
}
