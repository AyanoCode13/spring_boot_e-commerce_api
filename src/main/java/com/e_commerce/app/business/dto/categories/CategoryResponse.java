package com.e_commerce.app.business.dto.categories;

import lombok.*;

import java.util.List;


@Builder
public record CategoryResponse(
        Long id,
        String name,
        String description,
        Long parentId,
        String parentName,
        List<CategoryResponse> subCategories
) {}