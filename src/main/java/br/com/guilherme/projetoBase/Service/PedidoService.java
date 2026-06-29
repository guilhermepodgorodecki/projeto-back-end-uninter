package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.AtualizarStatusPedidoRequest;
import br.com.guilherme.projetoBase.DTO.CriarPedidoRequest;
import br.com.guilherme.projetoBase.DTO.PedidoDetalheResponse;
import br.com.guilherme.projetoBase.DTO.PedidoListResponse;
import br.com.guilherme.projetoBase.DTO.PedidoResponse;
import br.com.guilherme.projetoBase.Mapper.PedidoMapper;
import br.com.guilherme.projetoBase.Model.*;
import br.com.guilherme.projetoBase.Repository.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository    pedidoRepository;
    private final ProdutoRepository   produtoRepository;
    private final EstoqueRepository   estoqueRepository;
    private final UnidadeRepository   unidadeRepository;
    private final UserRepository      userRepository;
    private final PedidoMapper        pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         EstoqueRepository estoqueRepository,
                         UnidadeRepository unidadeRepository,
                         UserRepository userRepository,
                         PedidoMapper pedidoMapper) {
        this.pedidoRepository  = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.unidadeRepository = unidadeRepository;
        this.userRepository    = userRepository;
        this.pedidoMapper      = pedidoMapper;
    }

    @Transactional(readOnly = true)
    public List<PedidoListResponse> listarTodos(@Nullable CanalPedido canalPedido) {
        List<PedidoEntity> pedidos = canalPedido != null
                ? pedidoRepository.findByCanalPedido(canalPedido)
                : pedidoRepository.findAll();
        return pedidos.stream()
                .map(pedidoMapper::toPedidoListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoDetalheResponse buscarPorId(UUID id) {
        PedidoEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado: " + id));
        return pedidoMapper.toDetalheResponse(pedido);
    }

    @Transactional
    public PedidoDetalheResponse atualizarStatus(UUID id, AtualizarStatusPedidoRequest request) {
        PedidoEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido não encontrado: " + id));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException(
                    "Pedido com status " + pedido.getStatus() + " não pode ser alterado");
        }

        pedido.setStatus(request.status());
        return pedidoMapper.toDetalheResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request, String email) {

        UserEntity cliente = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));


        UnidadeEntity unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));

        PedidoEntity pedido = PedidoEntity.builder()
                .cliente(cliente)
                .unidade(unidade)
                .canalPedido(request.canalPedido())
                .itens(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CriarPedidoRequest.ItemRequest itemReq : request.itens()) {

            ProdutoEntity produto = produtoRepository.findById(itemReq.produtoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produto não encontrado: " + itemReq.produtoId()));

            if (!produto.getUnidade().getId().equals(unidade.getId())) {
                throw new RuntimeException(
                        "Produto " + produto.getNome() + " não pertence a esta unidade");
            }

            EstoqueEntity estoque = estoqueRepository
                    .findByProdutoIdAndUnidadeId(produto.getId(), unidade.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Estoque não encontrado para: " + produto.getNome()));

            if (estoque.getQuantidade() < itemReq.quantidade()) {
                throw new EstoqueInsuficienteException(
                        produto.getNome(),
                        estoque.getQuantidade(),
                        itemReq.quantidade());
            }

            estoque.setQuantidade(estoque.getQuantidade() - itemReq.quantidade());
            estoqueRepository.save(estoque);

            BigDecimal precoUnitario = produto.getValor();
            BigDecimal subtotal      = precoUnitario.multiply(
                    BigDecimal.valueOf(itemReq.quantidade()));

            ItensPedidoEntity item = ItensPedidoEntity.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(precoUnitario)
                    .subtotal(subtotal)
                    .build();

            pedido.getItens().add(item);
            total = total.add(subtotal);
        }

        pedido.setTotal(total);
        PedidoEntity salvo = pedidoRepository.save(pedido);

        return PedidoResponse.from(salvo);
    }

    public static class EstoqueInsuficienteException extends RuntimeException {
        public EstoqueInsuficienteException(String produto, int disponivel, int solicitado) {
            super("Estoque insuficiente para " + produto
                    + ": disponível=" + disponivel
                    + ", solicitado=" + solicitado);
        }
    }
}