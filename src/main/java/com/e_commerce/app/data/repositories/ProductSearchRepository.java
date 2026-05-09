package com.e_commerce.app.data.repositories;


import com.e_commerce.app.data.entities.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, Long> {
}