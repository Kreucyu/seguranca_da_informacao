package com.service.products.dto;

import java.math.BigDecimal;

public record CreateProductDTO(
        String name,
        String description,
        BigDecimal price,
        Integer quantity
) {
}

