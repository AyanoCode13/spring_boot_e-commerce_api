package com.e_commerce.app.data.repositories.cart;


import com.e_commerce.app.domain.entities.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserId(Long userId);
}