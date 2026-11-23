package com.scenara.backend.users.controller;

import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints do próprio usuário autenticado. O ID vem sempre do JWT, nunca da URL,
 * para que um usuário não consiga ler, alterar ou excluir a conta de outro.
 */
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    public ResponseEntity<CreateUserResponse> exibir(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(service.exibir(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<CreateUserResponse> atualizar(@AuthenticationPrincipal String userId,
                                                        @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(service.atualizar(userId, request));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@AuthenticationPrincipal String userId) {
        service.deletar(userId);
    }
}
