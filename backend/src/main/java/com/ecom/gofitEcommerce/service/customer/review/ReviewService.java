package com.ecom.gofitEcommerce.service.customer.review;

import com.ecom.gofitEcommerce.DTO.OrderedProductResponseDto;
import com.ecom.gofitEcommerce.DTO.ProductDto;
import com.ecom.gofitEcommerce.DTO.ReviewDto;
import com.ecom.gofitEcommerce.entity.*;
import com.ecom.gofitEcommerce.repository.OrderRepository;
import com.ecom.gofitEcommerce.repository.ProductRepository;
import com.ecom.gofitEcommerce.repository.ReviewRepository;
import com.ecom.gofitEcommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Value("${app.upload.review-dir:reviews}")
    private String reviewDir;

    private final OrderRepository orderRepository;

    private  final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;


    @Transactional
    public OrderedProductResponseDto getOrderedProductDetailsByOrderId(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderedProductResponseDto responseDto = new OrderedProductResponseDto();

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            responseDto.setOrderAmount(order.getAmount());

            List<ProductDto> productDtoList = new ArrayList<>();

            for (CartItems cartItem : order.getCartItems()) {
                Product product = cartItem.getProduct();

                ProductDto dto = new ProductDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(cartItem.getPrice());
                dto.setQuantity(cartItem.getQuantity());
                dto.setImgUrl(product.getImgUrl());

                productDtoList.add(dto);
            }

            responseDto.setProductDtoList(productDtoList);
        }

        return responseDto;
    }


    @Transactional
    public ReviewDto saveReview(ReviewDto reviewDto) throws IOException {
        Optional<User> userOpt = userRepository.findById(reviewDto.getUserId());
        Optional<Product> productOpt = productRepository.findById(reviewDto.getProductId());

        if (userOpt.isEmpty() || productOpt.isEmpty()) {
            return null;
        }
        MultipartFile imgFile = reviewDto.getImg();

        String imgUrl = null;
        if (imgFile != null && !imgFile.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + imgFile.getOriginalFilename();
            Path target = Paths.get(reviewDir).resolve(filename);
            Files.createDirectories(target.getParent());
            Files.copy(imgFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            imgUrl = "/reviews/" + filename;
        }

        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setDescription(reviewDto.getDescription());
        review.setImgUrl(imgUrl);
        review.setUser(userOpt.get());
        review.setProduct(productOpt.get());

        return reviewRepository.save(review).getDto();
    }
}
