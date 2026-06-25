package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.ProdutoDetalheResponse;
import br.com.guilherme.projetoBase.DTO.ProdutoListResponse;
import br.com.guilherme.projetoBase.DTO.ProdutoRequest;
import br.com.guilherme.projetoBase.DTO.ProdutoResponse;
import br.com.guilherme.projetoBase.Mapper.ProdutoMapper;
import br.com.guilherme.projetoBase.Model.ProdutoEntity;
import br.com.guilherme.projetoBase.Model.UnidadeEntity;
import br.com.guilherme.projetoBase.Repository.ProdutoRepository;
import br.com.guilherme.projetoBase.Repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoMapper     produtoMapper;

    @Transactional(readOnly = true)
    public List<ProdutoListResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toProdutoListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDetalheResponse buscarPorId(Integer id) {
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado: " + id));
        return produtoMapper.toDetalheResponse(produto);
    }

    public ProdutoResponse criaProduto(ProdutoRequest request) {
        UnidadeEntity unidade = unidadeRepository.findById(request.unidade())
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada: " + request.unidade()));

        ProdutoEntity produto = ProdutoEntity.builder()
                .nome(request.nome())
                .valor(request.valor())
                .disponivel(request.disponivel())
                .unidade(unidade)
                .build();

        return ProdutoResponse.from(produtoRepository.save(produto));
    }
}
