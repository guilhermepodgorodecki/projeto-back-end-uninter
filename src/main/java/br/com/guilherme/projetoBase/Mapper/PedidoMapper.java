package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.DTO.PedidoDetalheResponse;
import br.com.guilherme.projetoBase.DTO.PedidoListResponse;
import br.com.guilherme.projetoBase.Model.ItensPedidoEntity;
import br.com.guilherme.projetoBase.Model.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(source = "cliente.nome", target = "clienteNome")
    @Mapping(source = "unidade.nome", target = "unidadeNome")
    @Mapping(target = "quantidadeItens", expression = "java(pedido.getItens() != null ? pedido.getItens().size() : 0)")
    PedidoListResponse toPedidoListResponse(PedidoEntity pedido);

    @Mapping(source = "cliente.nome", target = "clienteNome")
    @Mapping(source = "unidade.nome", target = "unidadeNome")
    PedidoDetalheResponse toDetalheResponse(PedidoEntity pedido);

    @Mapping(source = "produto.nome", target = "produtoNome")
    PedidoDetalheResponse.ItemDetalheResponse toItemDetalheResponse(ItensPedidoEntity item);
}
