package br.com.guilherme.projetoBase.DTO;

public record EstoqueListResponse(
        String produtoNome,
        String unidadeNome,
        Integer quantidade
) {}
