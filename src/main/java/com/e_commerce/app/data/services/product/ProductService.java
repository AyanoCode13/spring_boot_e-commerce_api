package com.e_commerce.app.data.services.product;

import com.e_commerce.app.config.cache.CacheConstants;
import com.e_commerce.app.data.dto.product.ProductFilterRequest;
import com.e_commerce.app.data.dto.product.ProductSpecification;
import com.e_commerce.app.data.entities.product.ProductDocument;
import com.e_commerce.app.data.entities.product.ProductEntity;
import com.e_commerce.app.data.repositories.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchService productSearchService;

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS, allEntries = true)
    })
    @Transactional
    public ProductEntity create(ProductEntity product) {
        ProductEntity saved = productRepository.save(product);
        productSearchService.indexProduct(saved);
        return saved;
    }
    @Cacheable(value = CacheConstants.PRODUCT, key = "#id")
    @Transactional(readOnly = true)
    public ProductEntity getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }


    @Transactional(readOnly = true)
    public Page<ProductDocument> search(ProductFilterRequest filter) {
        return productSearchService.search(filter);
    }

    @Transactional(readOnly = true)
    public Page<ProductEntity> filter(ProductFilterRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                filter.toSort()
        );
        return productRepository.findAll(
                ProductSpecification.withFilters(filter), pageable);
    }

    // ── Update — evict both single product and list caches ────────────────
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCT, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCTS, allEntries = true)
    })
    public ProductEntity update(Long id, ProductEntity updated) {
        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setImageUrl(updated.getImageUrl());
        ProductEntity saved = productRepository.save(existing);
        productSearchService.indexProduct(saved);
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
        productSearchService.removeProduct(id);

    }
}