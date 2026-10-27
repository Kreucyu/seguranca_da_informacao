package com.exemplo.hmacapi.produto;

import java.math.BigDecimal;

public record ProductResponseDTO(Long id, String name, String description, BigDecimal price, Integer quantity) {

    public static ProductResponseDTO getNew(Product p) {
        return new ProductResponseDTO(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getQuantity());
    }
}
