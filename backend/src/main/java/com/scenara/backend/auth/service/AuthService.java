package com.scenara.backend.auth.service;

import com.scenara.backend.auth.dto.LoginRequest;
import com.scenara.backend.auth.dto.LoginResponse;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        UserModel user = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        return new LoginResponse(jwtService.generateToken(user));

    }
}
