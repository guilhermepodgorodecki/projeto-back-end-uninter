package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.FormaDePagamento;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PagamentoRequest(

        @NotNull(message = "pedidoId é obrigatório")
        UUID pedidoId,

        @NotNull(message = "metodo é obrigatório")
        FormaDePagamento metodo
) {}