package com.e_commerce.app.data.mappers;

import com.e_commerce.app.data.dto.categories.CategoryResponse;
import com.e_commerce.app.data.dto.product.ProductResponse;
import com.e_commerce.app.data.entities.category.CategoryEntity;
import com.e_commerce.app.data.entities.product.ProductEntity;
import org.springframework.stereotype.Component;

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



}