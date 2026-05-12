package com.e_commerce.app.business.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(@NotBlank String shippingAddress) {}