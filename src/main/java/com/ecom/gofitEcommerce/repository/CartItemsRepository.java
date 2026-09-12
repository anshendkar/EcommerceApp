package com.ecom.gofitEcommerce.repository;

import com.ecom.gofitEcommerce.entity.CartItems;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemsRepository  extends JpaRepository<CartItems , Long> {


    Optional<CartItems> findByProductIdAndOrderIdAndUserId(Long productId, Long orderId, Long userId);

    @Transactional
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
