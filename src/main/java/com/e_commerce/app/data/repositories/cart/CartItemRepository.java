package com.e_commerce.app.data.repositories.cart;


import com.e_commerce.app.domain.entities.cart.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteByCartId(Long cartId);
}