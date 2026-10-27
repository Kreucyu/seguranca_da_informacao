package com.service.products.dto;

import java.math.BigDecimal;

public record RecoveryProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity
) {
}

