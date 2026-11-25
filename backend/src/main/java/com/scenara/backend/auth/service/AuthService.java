package com.scenara.backend.auth.service;

import com.scenara.backend.auth.dto.LoginRequest;
import com.scenara.backend.auth.dto.LoginResponse;
import com.scenara.backend.users.Mapper.UserMapper;
import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.model.UserRole;
import com.scenara.backend.users.repository.UserRepository;
import io.jsonwebtoken.JwtException;
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
    private final SessionService sessionService;
    private final LoginAttemptService loginAttemptService;

    public LoginResponse login(LoginRequest request, String ip) {
        loginAttemptService.verificarBloqueio(request.email(), ip);

        UserModel user = repository.findByEmail(request.email())
                .filter(found -> encoder.matches(request.password(), found.getPassword()))
                .orElse(null);

        // E-mail inexistente e senha errada contam igual, para o limite não revelar quais e-mails existem.
        if (user == null) {
            loginAttemptService.registrarFalha(request.email(), ip);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        loginAttemptService.registrarSucesso(request.email());

        String sessionId = sessionService.criar(user.getId());
        return new LoginResponse(jwtService.generateToken(user, sessionId));
    }

    /** Encerra a sessão do token informado. É idempotente: token inválido ou já revogado não gera erro. */
    public void logout(String token) {
        try {
            String sessionId = jwtService.parseClaims(token).getId();
            if (sessionId != null) {
                sessionService.revogar(sessionId);
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            // nada a encerrar
        }
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
