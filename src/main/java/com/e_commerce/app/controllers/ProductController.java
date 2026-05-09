package com.e_commerce.app.controllers;


import com.e_commerce.app.data.dto.product.ProductFilterRequest;
import com.e_commerce.app.data.dto.product.ProductRequest;
import com.e_commerce.app.data.dto.product.ProductResponse;
import com.e_commerce.app.data.entities.ProductEntity;
import com.e_commerce.app.data.mappers.EntityMapper;
import com.e_commerce.app.services.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final EntityMapper mapper;

    // ── Filter via DB Specification ────────────────────────────────────────
    @Operation(summary = "Filter products from database",
            description = "Supports filtering by keyword, category, price range, and stock")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> filter(ProductFilterRequest filter) {

        return ResponseEntity.ok(
                productService.filter(filter).map(mapper::toProductResponse));
    }
    @GetMapping("/search")
    public ResponseEntity<?> search(ProductFilterRequest filter) {
        return ResponseEntity.ok(productService.search(filter));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toProductResponse(productService.getById(id)));
    }

    //@Operation(summary = "Create a new product", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {

        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())

                .build();
        return ResponseEntity.ok(mapper.toProductResponse(productService.create(product)));
    }

    @Operation(summary = "Update a product", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductEntity updated = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())

                .build();
        return ResponseEntity.ok(mapper.toProductResponse(productService.update(id, updated)));
    }

    @Operation(summary = "Delete a product", security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}