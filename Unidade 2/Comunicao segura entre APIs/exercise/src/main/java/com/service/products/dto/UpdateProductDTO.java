package com.service.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductDTO(
                @NotBlank(message = "Nome do produto é obrigatório") @Size(max = 120, message = "Nome do produto deve ter no máximo 120 caracteres") String name,

                @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres") String description,

                @NotNull(message = "Preço do produto é obrigatório") @DecimalMin(value = "0.00", message = "Preço não pode ser negativo") BigDecimal price,

                @NotNull(message = "Quantidade do produto é obrigatória") @Min(value = 0, message = "Quantidade não pode ser negativa") Integer quantity) {
}
