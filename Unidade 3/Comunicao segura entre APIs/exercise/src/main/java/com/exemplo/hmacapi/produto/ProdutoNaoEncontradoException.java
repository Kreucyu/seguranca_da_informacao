package com.exemplo.hmacapi.produto;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto " + id + " não encontrado");
    }
}
