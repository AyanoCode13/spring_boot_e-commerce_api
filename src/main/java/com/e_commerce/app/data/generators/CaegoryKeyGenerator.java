package com.e_commerce.app.data.generators;

import com.e_commerce.app.data.dto.product.ProductFilterRequest;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("categoryKeyGenerator")
public class CaegoryKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target,
                           Method method,
                           Object... params) {

        ProductFilterRequest f = (ProductFilterRequest) params[0];

        return String.join(":",
                safe(f.getKeyword())

        );
    }

    private String safe(Object value) {
        return value == null ? "null" : value.toString();
    }
}
