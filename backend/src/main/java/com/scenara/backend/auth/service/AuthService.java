package com.scenara.backend.auth.service;

import com.scenara.backend.auth.dto.LoginRequest;
import com.scenara.backend.auth.dto.LoginResponse;
import com.scenara.backend.users.Mapper.UserMapper;
import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.model.UserRole;
import com.scenara.backend.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final UserMapper mapper;

    public LoginResponse login(LoginRequest request) {
        UserModel user = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        return new LoginResponse(jwtService.generateToken(user));

    }


    public CreateUserResponse criar(CreateUserRequest request) {
        UserModel user = new UserModel();
        Instant now = Instant.now();

        if (repository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setUpdatedAt(now);
        user.setCreatedAt(now);


        return mapper.toResponse(repository.save(user));
    }
}
