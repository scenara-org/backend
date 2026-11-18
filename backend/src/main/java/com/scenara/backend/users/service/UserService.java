package com.scenara.backend.users.service;

import com.scenara.backend.users.Mapper.UserMapper;
import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public CreateUserResponse criar(CreateUserRequest request) {
        UserModel user = new UserModel();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        UserModel userSave = repository.save(user);

        return new CreateUserResponse(
                userSave.getId(),
                userSave.getName(),
                userSave.getEmail(),
                userSave.getCreatedAt(),
                userSave.getUpdatedAt()
        );
    }

    public CreateUserResponse exibir(String id) {
        UserModel user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário nao encontrado."));

        return new CreateUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public List<CreateUserResponse> exibirTodos() {
        return repository.findAll().stream()
                .map(user -> new CreateUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )).toList();
    }

    public CreateUserResponse atualizar(String id, UpdateUserRequest updateRequest) {
        UserModel user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        mapper.update(updateRequest, user);

        return mapper.toResponse(repository.save(user));
    }

    public void deletar(String id) {
        repository.deleteById(id);
    }

    }
