package com.e_commerce.app.data.services.order;

import com.e_commerce.app.config.exceptions.BadRequestException;
import com.e_commerce.app.data.dto.order.OrderRequest;
import com.e_commerce.app.data.entities.auth.UserEntity;
import com.e_commerce.app.data.entities.cart.CartEntity;
import com.e_commerce.app.data.entities.order.OrderEntity;

import com.e_commerce.app.data.entities.order.OrderItemEntity;
import com.e_commerce.app.data.enums.OrderStatus;
import com.e_commerce.app.data.repositories.auth.UserRepository;
import com.e_commerce.app.data.repositories.order.OrderRepository;
import com.e_commerce.app.data.services.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderEntity placeOrder(String email, OrderRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartEntity cart = cartService.getOrCreateCart(email);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with empty cart");
        }

        // Build order items from cart
        List<OrderItemEntity> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    // Check stock at order time
                    if (cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()) {
                        throw new BadRequestException(
                                "Insufficient stock for: " + cartItem.getProduct().getName());
                    }
                    // Deduct stock
                    cartItem.getProduct().setStockQuantity(
                            cartItem.getProduct().getStockQuantity() - cartItem.getQuantity());

                    return OrderItemEntity.builder()
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(cartItem.getProduct().getPrice())
                            .build();
                })
                .toList();

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(total)
                .items(orderItems)
                .build();

        // Link items back to order
        orderItems.forEach(item -> item.setOrder(order));

        OrderEntity saved = orderRepository.save(order);

        // Clear cart after successful order
        cartService.clearCart(cart);

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> getUserOrders(String email, int page, int size) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserId(
                user.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Transactional(readOnly = true)
    public OrderEntity getById(Long id, String email) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Order does not belong to this user");
        }
        return order;
    }

    @Transactional
    public OrderEntity updateStatus(Long id, OrderStatus status) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}