package com.scenara.backend.users.service;

import com.scenara.backend.users.Mapper.UserMapper;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public CreateUserResponse exibir(String id) {
        return mapper.toResponse(buscar(id));
    }

    public CreateUserResponse atualizar(String id, UpdateUserRequest updateRequest) {
        UserModel user = buscar(id);

        String novoEmail = updateRequest.email();
        if (novoEmail != null && !novoEmail.equalsIgnoreCase(user.getEmail())
                && repository.existsByEmail(novoEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }

        mapper.update(updateRequest, user);
        user.setUpdatedAt(Instant.now());
        return mapper.toResponse(repository.save(user));
    }

    public void deletar(String id) {
        repository.delete(buscar(id));
    }

    public UserModel buscar(String id) {
        return repository.findById(id).orElseThrow(this::naoEncontrado);
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
    }
}
