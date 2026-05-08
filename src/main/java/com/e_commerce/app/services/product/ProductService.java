package com.e_commerce.app.services.product;

import com.e_commerce.app.data.dto.product.ProductFilterRequest;
import com.e_commerce.app.data.dto.product.ProductSpecification;
import com.e_commerce.app.data.entities.ProductEntity;
import com.e_commerce.app.data.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    @Transactional
    public ProductEntity create(ProductEntity product) {
        ProductEntity saved = productRepository.save(product);

        return saved;
    }

    @Transactional(readOnly = true)
    public ProductEntity getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
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
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);

    }
}