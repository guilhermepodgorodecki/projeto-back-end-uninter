package br.com.guilherme.projetoBase.DTO;

import java.math.BigDecimal;

public record ProdutoRequest (String nome, BigDecimal valor, Boolean disponivel, Integer unidade){ }
