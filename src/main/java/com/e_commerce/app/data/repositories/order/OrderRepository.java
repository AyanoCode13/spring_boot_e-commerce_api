package com.e_commerce.app.data.repositories.order;

import com.e_commerce.app.domain.entities.order.OrderEntity;
import com.e_commerce.app.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable); // admin use
    Optional<OrderEntity> findByStripeSessionId(String stripeSessionId);
    Optional<OrderEntity> findByStripePaymentIntent(String stripePaymentIntent);
}