package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.PagamentoRequest;
import br.com.guilherme.projetoBase.DTO.PagamentoResponse;
import br.com.guilherme.projetoBase.Mapper.PagamentoRepository;
import br.com.guilherme.projetoBase.Model.*;
import br.com.guilherme.projetoBase.Repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository    pedidoRepository;
    private final GatewayMockService  gatewayMockService;

    @Transactional
    public PagamentoResponse processar(PagamentoRequest request) {

        // 1. busca o pedido
        PedidoEntity pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado: " + request.pedidoId()));

        // 2. valida se o pedido está no status correto
        if (pedido.getStatus() != StatusPedido.RECEBIDO) {
            throw new IllegalArgumentException(
                    "Pedido não está elegível para pagamento. Status atual: "
                            + pedido.getStatus()
            );
        }

        // 3. valida se já existe pagamento ativo
        if (pagamentoRepository.existsByPedidoId(request.pedidoId())) {
            throw new IllegalArgumentException("Pedido já possui pagamento ativo");
        }

        // 4. chama o gateway mock
        GatewayMockService.GatewayResponse gatewayResponse = gatewayMockService.processar(
                pedido.getId().toString(),
                request.metodo().name(),
                pedido.getTotal().doubleValue()
        );

        // 5. cria o registro de pagamento
        PagamentoEntity pagamento = PagamentoEntity.builder()
                .pedido(pedido)
                .metodo(request.metodo())
                .status(gatewayResponse.status())
                .valor(pedido.getTotal())
                .gatewayRef(gatewayResponse.gatewayRef())
                .processadoEm(LocalDateTime.now())
                .build();

        pagamentoRepository.save(pagamento);

        // 6. atualiza o statusPagamento no pedido
        pedido.setStatusPagamento(gatewayResponse.status());
        pedidoRepository.save(pedido);

        return PagamentoResponse.from(pagamento, gatewayResponse.mensagem());
    }

    public PagamentoResponse consultar(UUID pedidoId) {
        PagamentoEntity pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado para o pedido: " + pedidoId));

        return PagamentoResponse.from(pagamento, null);
    }
}