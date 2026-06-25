package br.com.guilherme.projetoBase.DTO;

import java.time.LocalDateTime;

public record EstoqueRequest(Integer produtoId, Integer unidadeId, Integer quantidade, LocalDateTime atualizado) { }
