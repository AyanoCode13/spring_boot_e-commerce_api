package com.e_commerce.app.data.dto.categories;

public record CreateCategoryRequest(
        String name,
        String description,
        Long parentId
) {}
