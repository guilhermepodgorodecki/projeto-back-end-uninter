package br.com.guilherme.projetoBase.DTO;

import java.math.BigDecimal;

public record ProdutoDetalheResponse(
        Integer id,
        String nome,
        BigDecimal valor,
        Boolean disponivel,
        String unidadeNome
) {}
