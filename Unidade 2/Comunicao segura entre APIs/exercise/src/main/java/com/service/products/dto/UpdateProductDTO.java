package com.service.products.dto;

import java.math.BigDecimal;

public record UpdateProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity
) {
}

