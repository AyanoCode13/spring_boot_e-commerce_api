package com.e_commerce.app.data.repositories.product;


import com.e_commerce.app.data.entities.product.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, Long> {
}