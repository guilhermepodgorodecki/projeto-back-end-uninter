package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.Model.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<PagamentoEntity, UUID> {
    Optional<PagamentoEntity> findByPedidoId(UUID pedidoId);
    boolean existsByPedidoId(UUID pedidoId);
}
