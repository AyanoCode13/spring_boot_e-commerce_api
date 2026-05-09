package com.e_commerce.app.data.dto.product;


import com.e_commerce.app.data.entities.product.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<ProductEntity> withFilters(ProductFilterRequest filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Keyword search on name and description ─────────────────────
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(nameLike, descLike));
            }

            // ── Category filter ────────────────────────────────────────────
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("category").get("id"),
                        filter.getCategoryId()
                ));
            }

            // ── Price range ────────────────────────────────────────────────
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"),
                        filter.getMinPrice()
                ));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"),
                        filter.getMaxPrice()
                ));
            }

            // ── In-stock only ──────────────────────────────────────────────
            if (Boolean.TRUE.equals(filter.getInStockOnly())) {
                predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
            }



            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}