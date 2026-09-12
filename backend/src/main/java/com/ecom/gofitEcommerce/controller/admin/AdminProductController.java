package com.ecom.gofitEcommerce.controller.admin;


import com.ecom.gofitEcommerce.DTO.FAQDto;
import com.ecom.gofitEcommerce.DTO.ProductDto;
import com.ecom.gofitEcommerce.DTO.ProductPageResponse;
import com.ecom.gofitEcommerce.service.admin.AdminProductService;
import com.ecom.gofitEcommerce.service.admin.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    private final FAQService faqService;

    @PostMapping(path="/addproduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> addProduct(@ModelAttribute ProductDto dto) throws IOException {
        ProductDto saved = adminProductService.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping(path="/products")
    public ResponseEntity<ProductPageResponse> listProducts(
            @PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminProductService.getProducts(pageable));

    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductDto>> getAllProductsByname(@PathVariable String name){
        List<ProductDto> productDtos = adminProductService.getAllProductsByname(name);
        return ResponseEntity.ok(productDtos);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId){
        boolean deleted = adminProductService.deleteProduct(productId);
        if(deleted){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/faq/{productId}")
    public ResponseEntity<FAQDto> postFaq(@PathVariable Long productId , @RequestBody FAQDto faqDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.postFAQ(productId , faqDto));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId){
        ProductDto productDto = adminProductService.getProductById(productId);
        if(productDto != null){
            return ResponseEntity.ok(productDto);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId , @ModelAttribute ProductDto productDto){
        ProductDto updatedproduct = adminProductService.updateProduct(productId , productDto);
        if(updatedproduct != null){
            return ResponseEntity.ok(updatedproduct);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
