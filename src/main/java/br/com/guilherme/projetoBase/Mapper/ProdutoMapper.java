package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.DTO.ProdutoDetalheResponse;
import br.com.guilherme.projetoBase.DTO.ProdutoListResponse;
import br.com.guilherme.projetoBase.Model.ProdutoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoListResponse toProdutoListResponse(ProdutoEntity produto);

    @Mapping(source = "unidade.nome", target = "unidadeNome")
    ProdutoDetalheResponse toDetalheResponse(ProdutoEntity produto);
}
