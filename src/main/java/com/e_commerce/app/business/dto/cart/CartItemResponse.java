package com.e_commerce.app.business.dto.cart;

import lombok.*;

import java.math.BigDecimal;


@Builder
public record CartItemResponse(
         Long id,
         Long productId,
         String productName,
         String imageUrl,
         BigDecimal price,
         Integer quantity,
         BigDecimal subtotal
) {

}