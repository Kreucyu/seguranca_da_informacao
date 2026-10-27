package com.exemplo.hmacapi.produto;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Produto " + id + " não encontrado");
    }
}
