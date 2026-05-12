package com.e_commerce.app.business.services.category;


import com.e_commerce.app.config.cache.CacheConstants;

import com.e_commerce.app.domain.entities.category.CategoryEntity;
import com.e_commerce.app.data.repositories.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id));
    }

    // ── Create — evict category list cache ────────────────────────────────
    @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true)
    @Transactional
    public CategoryEntity create(CategoryEntity category) {

        CategoryEntity saved = categoryRepository.save(category);
        log.info("Saved category with ID: {}", saved.getId()); // ← add this
        return saved;
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