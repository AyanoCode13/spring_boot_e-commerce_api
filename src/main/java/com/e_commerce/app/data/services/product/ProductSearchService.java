package com.e_commerce.app.data.services.product;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.e_commerce.app.data.dto.product.ProductFilterRequest;
import com.e_commerce.app.data.entities.product.ProductDocument;
import com.e_commerce.app.data.entities.product.ProductEntity;
import com.e_commerce.app.data.repositories.product.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(
        name = "spring.data.elasticsearch.repositories.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository productSearchRepository;

    @Transactional(readOnly = true)
    public void indexProduct(ProductEntity product) {
        productSearchRepository.save(toDocument(product));
    }

    public void removeProduct(Long productId) {
        productSearchRepository.deleteById(productId);
    }


    public Page<ProductDocument> search(ProductFilterRequest filter) {

        List<Query> mustQueries = new ArrayList<>();

        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            mustQueries.add(Query.of(q -> q
                    .multiMatch(mm -> mm
                            .query(filter.getKeyword())
                            .fields("name^2", "description")
                    )
            ));
        }


        if (Boolean.TRUE.equals(filter.getInStockOnly())) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .number(n -> n
                                    .field("stockQuantity")
                                    .gt(0.0)
                            )
                    )
            ));
        }

        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            mustQueries.add(Query.of(q -> q
                    .range(r -> r
                            .number(n -> {
                                n.field("price");
                                if (filter.getMinPrice() != null)
                                    n.gte(filter.getMinPrice().doubleValue());
                                if (filter.getMaxPrice() != null)
                                    n.lte(filter.getMaxPrice().doubleValue());
                                return n;
                            })
                    )
            ));
        }

        Query finalQuery = mustQueries.isEmpty()
                ? Query.of(q -> q.matchAll(m -> m))
                : Query.of(q -> q.bool(b -> b.must(mustQueries)));

        String sortField = filter.getSortBy() != null ? filter.getSortBy() : "createdAt";
        SortOrder sortOrder = "asc".equalsIgnoreCase(filter.getSortDirection())
                ? SortOrder.Asc : SortOrder.Desc;

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withSort(s -> s.field(f -> f.field(sortField).order(sortOrder)))
                .withPageable(pageable)
                .build();

        SearchHits<ProductDocument> hits =
                elasticsearchOperations.search(nativeQuery, ProductDocument.class);

        List<ProductDocument> results = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(results, pageable, hits.getTotalHits());
    }

    private ProductDocument toDocument(ProductEntity product) {
        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .build();
    }
}