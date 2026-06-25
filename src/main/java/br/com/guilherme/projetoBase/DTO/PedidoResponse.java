package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.CanalPedido;
import br.com.guilherme.projetoBase.Model.ItensPedidoEntity;  // ← confere esse import
import br.com.guilherme.projetoBase.Model.PedidoEntity;
import br.com.guilherme.projetoBase.Model.StatusPagamento;
import br.com.guilherme.projetoBase.Model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String unidade,
        CanalPedido canalPedido,
        StatusPedido status,
        StatusPagamento statusPagamento,
        BigDecimal total,
        List<ItemResponse> itens,
        LocalDateTime criadoEm
) {
    public record ItemResponse(
            String produto,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}

    public static PedidoResponse from(PedidoEntity p) {
        List<ItemResponse> itens = p.getItens().stream()
                .map(i -> new ItemResponse(
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                p.getId(),
                p.getUnidade().getNome(),
                p.getCanalPedido(),
                p.getStatus(),
                p.getStatusPagamento(),
                p.getTotal(),
                itens,
                p.getCriadoEm()
        );
    }
}