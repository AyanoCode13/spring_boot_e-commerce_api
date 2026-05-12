package com.e_commerce.app.data.generators;

import com.e_commerce.app.business.dto.product.ProductFilterRequest;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("productKeyGenerator")
public class ProductKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target,
                           Method method,
                           Object... params) {

        ProductFilterRequest f = (ProductFilterRequest) params[0];

        return String.join(":",
                safe(f.getKeyword()),
                safe(f.getSortBy()),
                safe(f.getSortDirection()),
                String.valueOf(f.getPage()),
                String.valueOf(f.getSize()),
                String.valueOf(f.getMinPrice()),
                String.valueOf(f.getMaxPrice()),
                String.valueOf(f.getInStockOnly())
        );
    }

    private String safe(Object value) {
        return value == null ? "null" : value.toString();
    }
}