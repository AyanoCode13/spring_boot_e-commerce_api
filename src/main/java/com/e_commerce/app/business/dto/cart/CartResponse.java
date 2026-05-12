package com.e_commerce.app.business.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Builder
public record CartResponse(
         Long id,
         List<CartItemResponse> items,
         BigDecimal totalPrice,
         Integer totalItems
) { }