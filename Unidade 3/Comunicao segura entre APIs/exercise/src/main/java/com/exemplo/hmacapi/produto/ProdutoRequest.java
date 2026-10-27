package com.exemplo.hmacapi.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 500) String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal preco,
        @NotNull @Min(0) Integer quantidade) {
}
