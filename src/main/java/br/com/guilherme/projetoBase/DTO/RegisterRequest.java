package br.com.guilherme.projetoBase.DTO;

import br.com.guilherme.projetoBase.Model.Role;

public record RegisterRequest(String nome, String email, String password, Role role) {}
