package com.e_commerce.app.config.elasticsearch;


import com.e_commerce.app.data.repositories.product.ProductRepository;
import com.e_commerce.app.data.services.product.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
@ConditionalOnProperty(
        name = "spring.data.repositories.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ElasticsearchIndexInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ProductSearchService productSearchService;

    @Override
    @Transactional(readOnly = true)
    public void run(ApplicationArguments args) {
        log.info("Syncing products to Elasticsearch...");
        productRepository.findAll()
                .forEach(productSearchService::indexProduct);
        log.info("Elasticsearch sync complete.");
    }
}