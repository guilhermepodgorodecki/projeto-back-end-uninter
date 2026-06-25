package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.Model.StatusPagamento;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class GatewayMockService {

    private final Random random = new Random();

    public GatewayResponse processar(String pedidoId, String metodo, Double valor) {
        boolean aprovado = random.nextInt(10) != 0;

        return new GatewayResponse(
                aprovado ? StatusPagamento.EFETIVADO: StatusPagamento.NAO_EFETIVADO,
                aprovado ? "mock-ref-" + UUID.randomUUID() : null,
                aprovado ? "Pagamento aprovado" : "Saldo insuficiente"
        );
    }

    public record GatewayResponse(
            StatusPagamento status,
            String gatewayRef,
            String mensagem
    ) {}
}