package br.com.guilherme.projetoBase.Service;

import br.com.guilherme.projetoBase.DTO.RegisterRequest;
import br.com.guilherme.projetoBase.DTO.UserDto;
import br.com.guilherme.projetoBase.Model.UserEntity;
import br.com.guilherme.projetoBase.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.guilherme.projetoBase.Mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserEntity register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + request.email());
        }

        UserEntity user = UserEntity.builder()
                .nome(request.nome())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        return userRepository.save(user);
    }

    public List<UserDto> getTodos() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
