package com.ecom.gofitEcommerce.repository;

import com.ecom.gofitEcommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist , Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findAllByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);


}
