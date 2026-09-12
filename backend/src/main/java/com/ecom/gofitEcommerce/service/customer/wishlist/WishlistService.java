package com.ecom.gofitEcommerce.service.customer.wishlist;

import com.ecom.gofitEcommerce.DTO.WishlistDto;
import com.ecom.gofitEcommerce.entity.Product;
import com.ecom.gofitEcommerce.entity.User;
import com.ecom.gofitEcommerce.entity.Wishlist;
import com.ecom.gofitEcommerce.repository.ProductRepository;
import com.ecom.gofitEcommerce.repository.UserRepository;
import com.ecom.gofitEcommerce.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final WishlistRepository wishlistRepository;

    @Transactional
    public WishlistDto addProducttoWishlist(WishlistDto dto) {

        // ✅ check duplicate
        boolean exists = wishlistRepository
                .existsByUserIdAndProductId(dto.getUserId(), dto.getProductId());

        if (exists) {
            return null; // already exists
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setProduct(product);
        wishlist.setUser(user);

        Wishlist saved = wishlistRepository.save(wishlist);

        return saved.getWishlistDto();
    }

    @Transactional
    public List<WishlistDto> getWishlistByUserId(Long userId) {
        return wishlistRepository.findAllByUserId(userId).stream().map(Wishlist::getWishlistDto).collect(Collectors.toList());
    }

    @Transactional
    public void removeProductFromWishlist(Long userId, Long productId) {

        boolean exists =
                wishlistRepository.existsByUserIdAndProductId(userId, productId);

        if (!exists) {
            throw new RuntimeException("Product not found in wishlist");
        }

        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
