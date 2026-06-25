package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.DTO.UnidadeDto;
import br.com.guilherme.projetoBase.Model.UnidadeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnidadeMapper {
    UnidadeDto toUnidadeDto(UnidadeEntity unidade);
}
