package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.ProdutoEntity;

import java.math.BigDecimal;

public record ProdutoResponse(
        Integer id,
        String  nome,
        BigDecimal valor,
        Boolean disponivel,
        Integer unidade
) {
    public static ProdutoResponse from(ProdutoEntity p){
        return new ProdutoResponse(
                p.getId(),
                p.getNome(),
                p.getValor(),
                p.getDisponivel(),
                p.getUnidade().getId()
        );
    }
}
