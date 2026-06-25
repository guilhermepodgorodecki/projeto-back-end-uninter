package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.EstoqueDetalheResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueListResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueRequest;
import br.com.guilherme.projetoBase.DTO.EstoqueResponse;
import br.com.guilherme.projetoBase.DTO.ReporEstoqueRequest;
import br.com.guilherme.projetoBase.Mapper.EstoqueMapper;
import br.com.guilherme.projetoBase.Model.EstoqueEntity;
import br.com.guilherme.projetoBase.Model.ProdutoEntity;
import br.com.guilherme.projetoBase.Model.UnidadeEntity;
import br.com.guilherme.projetoBase.Repository.EstoqueRepository;
import br.com.guilherme.projetoBase.Repository.ProdutoRepository;
import br.com.guilherme.projetoBase.Repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;
    private final EstoqueMapper     estoqueMapper;

    @Transactional(readOnly = true)
    public List<EstoqueListResponse> listarEstoque(Integer produtoId, Integer unidadeId) {
        List<EstoqueEntity> estoques;

        if (produtoId != null && unidadeId != null) {
            estoques = estoqueRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId)
                    .stream().toList();
        } else if (produtoId != null) {
            estoques = estoqueRepository.findAllByProdutoId(produtoId);
        } else if (unidadeId != null) {
            estoques = estoqueRepository.findAllByUnidadeId(unidadeId);
        } else {
            estoques = estoqueRepository.findAll();
        }

        return estoques.stream()
                .map(estoqueMapper::toEstoqueListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstoqueDetalheResponse buscarPorId(Integer id) {
        EstoqueEntity estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque não encontrado: " + id));
        return estoqueMapper.toEstoqueDetalheResponse(estoque);
    }

    @Transactional
    public EstoqueDetalheResponse reporEstoque(Integer produtoId, ReporEstoqueRequest request) {
        EstoqueEntity estoque = estoqueRepository
                .findByProdutoIdAndUnidadeId(produtoId, request.unidadeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Estoque não encontrado para produto " + produtoId + " na unidade " + request.unidadeId()));

        estoque.setQuantidade(estoque.getQuantidade() + request.quantidade());
        estoque.setAtualizadoEm(LocalDateTime.now());

        return estoqueMapper.toEstoqueDetalheResponse(estoqueRepository.save(estoque));
    }

    @Transactional
    public EstoqueResponse adicionaEstoque(EstoqueRequest request) {
        ProdutoEntity produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado: " + request.produtoId()));
        UnidadeEntity unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada: " + request.unidadeId()));

        EstoqueEntity estoque = EstoqueEntity.builder()
                .produto(produto)
                .unidade(unidade)
                .quantidade(request.quantidade())
                .atualizadoEm(request.atualizado())
                .build();

        return EstoqueResponse.from(estoqueRepository.save(estoque));
    }
}
