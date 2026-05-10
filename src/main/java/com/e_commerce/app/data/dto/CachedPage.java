package com.e_commerce.app.data.dto;

import java.io.Serializable;
import java.util.List;

// Create a serializable wrapper
public record CachedPage<T>(List<T> content, long totalElements, int totalPages, int pageNumber)
        implements Serializable {}
