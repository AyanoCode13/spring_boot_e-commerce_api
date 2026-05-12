package com.e_commerce.app.business.dto.order;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) { }