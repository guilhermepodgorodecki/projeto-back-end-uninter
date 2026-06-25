package br.com.guilherme.projetoBase.DTO;

import java.time.LocalDateTime;

public record EstoqueDetalheResponse(
        Integer id,
        String produtoNome,
        String unidadeNome,
        Integer quantidade,
        LocalDateTime atualizadoEm
) {}
