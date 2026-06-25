package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoRequest(
        @NotNull(message = "Status é obrigatório")
        StatusPedido status
) {}
