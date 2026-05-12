package com.e_commerce.app.business.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
public record ProductResponse (
         Long id,
         String name,
         String description,
         BigDecimal price,
         Integer stockQuantity,
         String imageUrl,
         String categoryName,
         Long categoryId,
         LocalDateTime createdAt
){ }