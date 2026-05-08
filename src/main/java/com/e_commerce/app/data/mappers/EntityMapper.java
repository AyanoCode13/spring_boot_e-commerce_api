package com.e_commerce.app.data.mappers;

import com.e_commerce.app.data.dto.product.ProductResponse;
import com.e_commerce.app.data.entities.ProductEntity;
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



}