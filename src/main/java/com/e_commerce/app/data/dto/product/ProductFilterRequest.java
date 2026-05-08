package com.e_commerce.app.data.dto.product;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {

    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStockOnly;
    private String sortBy;
    private String sortDirection;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    public Sort toSort() {
        String field = sortBy != null ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}