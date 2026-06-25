package br.com.guilherme.projetoBase.Repository;

import br.com.guilherme.projetoBase.Model.CanalPedido;
import br.com.guilherme.projetoBase.Model.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {
    List<PedidoEntity> findByCanalPedido(CanalPedido canalPedido);
}
