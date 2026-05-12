package com.e_commerce.app.business.dto.categories;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        String description,
        Long parentId
) {}
