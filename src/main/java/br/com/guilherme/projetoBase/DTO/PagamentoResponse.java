package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.FormaDePagamento;
import br.com.guilherme.projetoBase.Model.PagamentoEntity;
import br.com.guilherme.projetoBase.Model.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoResponse(
        UUID id,
        UUID pedidoId,
        FormaDePagamento metodo,
        StatusPagamento status,
        BigDecimal valor,
        String gatewayRef,
        String mensagem,
        LocalDateTime processadoEm
) {
    public static PagamentoResponse from(PagamentoEntity p, String mensagem) {
        return new PagamentoResponse(
                p.getId(),
                p.getPedido().getId(),
                p.getMetodo(),
                p.getStatus(),
                p.getValor(),
                p.getGatewayRef(),
                mensagem,
                p.getProcessadoEm()
        );
    }
}