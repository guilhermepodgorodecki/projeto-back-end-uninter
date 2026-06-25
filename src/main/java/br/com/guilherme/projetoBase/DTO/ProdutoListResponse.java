package br.com.guilherme.projetoBase.DTO;

import java.math.BigDecimal;

public record ProdutoListResponse(
        Integer id,
        String nome,
        BigDecimal valor,
        Boolean disponivel
) {}
