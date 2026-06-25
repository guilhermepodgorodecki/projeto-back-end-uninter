package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.*;
import br.com.guilherme.projetoBase.Mapper.UnidadeMapper;
import br.com.guilherme.projetoBase.Model.UnidadeEntity;
import br.com.guilherme.projetoBase.Model.UserEntity;
import br.com.guilherme.projetoBase.Repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeMapper unidadeMapper;

    public UnidadeResponse criaUnidade(UnidadeRequest request){
        if (unidadeRepository.existsByCnpj(request.cnpj())){
            throw new IllegalArgumentException("Unidade já cadastrada: " + request.cnpj());
        }

        UnidadeEntity unidade = UnidadeEntity.builder()
                .endereco(request.endereco())
                .nome(request.nome())
                .cnpj(request.cnpj())
                .telefone(request.telefone())
                .build();

        return UnidadeResponse.from(unidadeRepository.save(unidade));
    }

    public List<UnidadeDto> getTodas() {
        return unidadeRepository.findAll()
                .stream()
                .map(unidadeMapper::toUnidadeDto)
                .toList();
    }
}
