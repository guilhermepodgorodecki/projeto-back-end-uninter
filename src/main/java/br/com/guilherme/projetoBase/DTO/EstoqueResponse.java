package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.EstoqueEntity;
import br.com.guilherme.projetoBase.Model.UnidadeEntity;

import java.time.LocalDateTime;

public record EstoqueResponse(
        Integer id,
        Integer produtoId,
        Integer unidadeId,
        Integer quantidade,
        LocalDateTime atualizadoEm
){
    public static EstoqueResponse from(EstoqueEntity e) {
        return new EstoqueResponse(
                e.getId(),
                e.getProduto().getId(),
                e.getUnidade().getId(),
                e.getQuantidade(),
                e.getAtualizadoEm()
        );
    }
}