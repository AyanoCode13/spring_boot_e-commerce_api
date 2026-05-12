package com.e_commerce.app.data.mappers;

import com.e_commerce.app.business.dto.cart.CartItemResponse;
import com.e_commerce.app.business.dto.cart.CartResponse;
import com.e_commerce.app.business.dto.categories.CategoryResponse;
import com.e_commerce.app.business.dto.order.OrderItemResponse;
import com.e_commerce.app.business.dto.order.OrderResponse;
import com.e_commerce.app.business.dto.product.ProductResponse;
import com.e_commerce.app.domain.entities.cart.CartEntity;
import com.e_commerce.app.domain.entities.cart.CartItemEntity;
import com.e_commerce.app.domain.entities.category.CategoryEntity;
import com.e_commerce.app.domain.entities.order.OrderEntity;
import com.e_commerce.app.domain.entities.order.OrderItemEntity;
import com.e_commerce.app.domain.entities.product.ProductEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class EntityMapper {

    // ── Product ────────────────────────────────────────────────────────────
    public ProductResponse toProductResponse(ProductEntity product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .build();
    }
    // ── Category ───────────────────────────────────────────────────────────
    public CategoryResponse toCategoryResponse(CategoryEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null
                        ? category.getParent().getId() : null)
                .parentName(category.getParent() != null
                        ? category.getParent().getName() : null)
                .subCategories(category.getSubCategories() != null
                        ? category.getSubCategories().stream()
                        .map(this::toCategoryResponse).toList()
                        : List.of())
                .build();
    }
    // ── Cart ───────────────────────────────────────────────────────────────
    public CartResponse toCartResponse(CartEntity cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalPrice(total)
                .totalItems(totalItems)
                .build();
    }

    public CartItemResponse toCartItemResponse(CartItemEntity item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .imageUrl(item.getProduct().getImageUrl())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    // ── Order ──────────────────────────────────────────────────────────────
    public OrderResponse toOrderResponse(OrderEntity order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }
    public OrderItemResponse toOrderItemResponse(OrderItemEntity item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

}