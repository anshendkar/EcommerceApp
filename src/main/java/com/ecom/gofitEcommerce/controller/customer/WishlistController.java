package com.ecom.gofitEcommerce.controller.customer;


import com.ecom.gofitEcommerce.DTO.WishlistDto;
import com.ecom.gofitEcommerce.service.customer.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/wishlist")
    public ResponseEntity<?> addProductTowishlist(@RequestBody WishlistDto wishlistDto){

        WishlistDto postedWishlistDto =
                wishlistService.addProducttoWishlist(wishlistDto);

        if(postedWishlistDto == null){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Product already in wishlist");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postedWishlistDto);
    }

    @GetMapping("/wishlist/{userId}")
    public ResponseEntity<List<WishlistDto>> getWishlistByUser(@PathVariable Long userId) {
        List<WishlistDto> wishlist = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/wishlist/{userId}/{productId}")
    public ResponseEntity<?> removeProductFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        wishlistService.removeProductFromWishlist(userId, productId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product removed from wishlist");

        return ResponseEntity.ok(response);
    }
}
