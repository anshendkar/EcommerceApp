package com.ecom.gofitEcommerce.controller.customer;

import com.ecom.gofitEcommerce.DTO.OrderedProductResponseDto;
import com.ecom.gofitEcommerce.DTO.ReviewDto;
import com.ecom.gofitEcommerce.service.customer.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/ordered-products/{orderId}")
    public ResponseEntity<OrderedProductResponseDto> getOrderedProductsDetailsByOrderId(@PathVariable Long orderId){
        return ResponseEntity.ok(reviewService.getOrderedProductDetailsByOrderId(orderId));
    }

    @PostMapping(path="/reviews/",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> giveReview(@ModelAttribute ReviewDto reviewDto) throws IOException {
        ReviewDto savedDto = reviewService.saveReview(reviewDto);
        if (savedDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

}
