package br.com.guilherme.projetoBase.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReporEstoqueRequest(
        @NotNull(message = "Unidade é obrigatória")
        Integer unidadeId,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade
) {}
