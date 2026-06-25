package br.com.guilherme.projetoBase.Repository;

import br.com.guilherme.projetoBase.Model.EstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<EstoqueEntity, Integer> {
    Optional<EstoqueEntity> findByProdutoIdAndUnidadeId(Integer produtoId, Integer unidadeId);
    List<EstoqueEntity> findAllByUnidadeId(Integer unidadeId);
    List<EstoqueEntity> findAllByProdutoId(Integer produtoId);
}