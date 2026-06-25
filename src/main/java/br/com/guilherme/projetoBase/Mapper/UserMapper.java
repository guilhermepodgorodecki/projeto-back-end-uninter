package br.com.guilherme.projetoBase.Mapper;

import br.com.guilherme.projetoBase.DTO.UserDto;
import br.com.guilherme.projetoBase.Model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "nome", target = "username")
    @Mapping(source = "email", target = "email")
    UserDto toDto(UserEntity entity);
}