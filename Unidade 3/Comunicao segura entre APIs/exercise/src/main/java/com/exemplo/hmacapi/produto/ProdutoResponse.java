package com.exemplo.hmacapi.produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, String descricao, BigDecimal preco, Integer quantidade) {

    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getQuantidade());
    }
}
