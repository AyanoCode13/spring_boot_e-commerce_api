package com.e_commerce.app.controllers;


import com.e_commerce.app.data.dto.categories.CategoryResponse;
import com.e_commerce.app.data.dto.categories.CreateCategoryRequest;
import com.e_commerce.app.data.entities.category.CategoryEntity;
import com.e_commerce.app.data.mappers.EntityMapper;
import com.e_commerce.app.data.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final EntityMapper mapper;
    @GetMapping("/add")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(
              "Hello"
        );
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(
                categoryService.getMainCategories().stream()
                        .map(mapper::toCategoryResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toCategoryResponse(categoryService.getById(id))
        );
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(request.name());
        entity.setDescription(request.description());
        mapper.toCategoryResponse(categoryService.create(entity));
        return ResponseEntity.ok(mapper.toCategoryResponse(entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}