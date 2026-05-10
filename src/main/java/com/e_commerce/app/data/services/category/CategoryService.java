package com.e_commerce.app.data.services.category;


import com.e_commerce.app.config.cache.CacheConstants;

import com.e_commerce.app.data.entities.category.CategoryEntity;
import com.e_commerce.app.data.repositories.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // ── Get all top-level categories — cached ──────────────────────────────
    @Cacheable(value = CacheConstants.CATEGORIES)
    @Transactional(readOnly = true)
    public List<CategoryEntity> getMainCategories() {
        return categoryRepository.findByParentIsNull();
    }

    // ── Get single category by ID — cached ────────────────────────────────
    @Cacheable(value = CacheConstants.CATEGORY, key = "#id")
    @Transactional(readOnly = true)
    public CategoryEntity getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));
    }

    // ── Create — evict category list cache ────────────────────────────────
    @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true)
    @Transactional
    public CategoryEntity create(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    // ── Delete — evict both single and list caches ────────────────────────
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CATEGORY, key = "#id"),
            @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true)
    })
    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}