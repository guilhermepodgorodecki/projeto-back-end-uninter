package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.CanalPedido;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriarPedidoRequest(

        @NotNull(message = "unidadeId é obrigatório")
        Integer unidadeId,

        @NotNull(message = "canalPedido é obrigatório")
        CanalPedido canalPedido,

        @NotEmpty(message = "pedido deve ter ao menos um item")
        List<ItemRequest> itens
) {
    public record ItemRequest(
            @NotNull(message = "produtoId é obrigatório")
            Integer produtoId,

            @NotNull(message = "quantidade é obrigatória")
            Integer quantidade
    ) {}
}
