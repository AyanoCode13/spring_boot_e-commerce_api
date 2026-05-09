package com.e_commerce.app.data.repositories.product;


import com.e_commerce.app.data.entities.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
        extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {  // ← add this
}