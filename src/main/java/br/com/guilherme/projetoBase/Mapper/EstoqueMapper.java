package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.DTO.EstoqueDetalheResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueListResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueRequest;
import br.com.guilherme.projetoBase.Model.EstoqueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "unidade.id", target = "unidadeId")
    @Mapping(source = "atualizadoEm", target = "atualizado")
    EstoqueRequest toEstoqueDto(EstoqueEntity estoque);

    @Mapping(source = "produto.nome", target = "produtoNome")
    @Mapping(source = "unidade.nome", target = "unidadeNome")
    EstoqueListResponse toEstoqueListResponse(EstoqueEntity estoque);

    @Mapping(source = "produto.nome", target = "produtoNome")
    @Mapping(source = "unidade.nome", target = "unidadeNome")
    EstoqueDetalheResponse toEstoqueDetalheResponse(EstoqueEntity estoque);
}
