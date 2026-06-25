package br.com.guilherme.projetoBase.Model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tab_unidades")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 18, unique = true)
    private String cnpj;

    @Column(length = 20)
    private String telefone;
}
