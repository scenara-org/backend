package com.scenara.backend.auth.controller;

import com.scenara.backend.auth.dto.LoginRequest;
import com.scenara.backend.auth.dto.LoginResponse;
import com.scenara.backend.auth.service.AuthService;
import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        // remoteAddr e não X-Forwarded-For: o cabeçalho é definido pelo cliente e burlaria o limite por IP.
        return ResponseEntity.ok(authService.login(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authService.logout(authorization.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CreateUserResponse> criar(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.criar(request));
    }
}
