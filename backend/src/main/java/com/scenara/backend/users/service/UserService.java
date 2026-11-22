package com.scenara.backend.users.service;

import com.scenara.backend.users.Mapper.UserMapper;
import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.model.UserRole;
import com.scenara.backend.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponse criar(CreateUserRequest request) {
        UserModel user = new UserModel();
        Instant now = Instant.now();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setUpdatedAt(now);
        user.setCreatedAt(now);


        return mapper.toResponse(repository.save(user));
    }

    public CreateUserResponse exibir(String id) {
        UserModel user = buscar(id);

        return mapper.toResponse(user);
    }

    public List<CreateUserResponse> exibirTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CreateUserResponse atualizar(String id, UpdateUserRequest updateRequest) {
        UserModel user = buscar(id);
        mapper.update(updateRequest, user);
        user.setUpdatedAt(Instant.now());
        return mapper.toResponse(repository.save(user));
    }

    public void deletar(String id) {
        repository.deleteById(id);
    }

    public UserModel buscar(String id) {
        return repository.findById(id).orElseThrow(this::naoEncontrado);
    }

    public ResponseStatusException naoEncontrado(){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
    }
    }
