package br.com.guilherme.projetoBase.Repository;

import br.com.guilherme.projetoBase.Model.UnidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UnidadeRepository extends JpaRepository<UnidadeEntity, Integer> {
    boolean existsByCnpj(String cnpj);
}
