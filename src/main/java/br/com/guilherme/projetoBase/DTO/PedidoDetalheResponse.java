package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.CanalPedido;
import br.com.guilherme.projetoBase.Model.StatusPagamento;
import br.com.guilherme.projetoBase.Model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoDetalheResponse(
        UUID id,
        String clienteNome,
        String unidadeNome,
        CanalPedido canalPedido,
        StatusPedido status,
        StatusPagamento statusPagamento,
        BigDecimal total,
        List<ItemDetalheResponse> itens,
        LocalDateTime criadoEm
) {
    public record ItemDetalheResponse(
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}
}
