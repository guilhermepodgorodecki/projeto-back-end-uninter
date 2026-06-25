package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.UnidadeEntity;

public record UnidadeResponse(
        Integer id,
        String  nome,
        String  endereco,
        String  cnpj,
        String  telefone
) {
    public static UnidadeResponse from(UnidadeEntity u) {
        return new UnidadeResponse(
                u.getId(),
                u.getNome(),
                u.getEndereco(),
                u.getCnpj(),
                u.getTelefone()
        );
    }
}