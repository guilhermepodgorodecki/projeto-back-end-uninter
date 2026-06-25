package br.com.guilherme.projetoBase.Repository;

import br.com.guilherme.projetoBase.Model.ProdutoEntity;
import br.com.guilherme.projetoBase.Model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Integer> { }
