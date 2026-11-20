package com.scenara.backend.users.controller;

import com.scenara.backend.users.dto.CreateUserRequest;
import com.scenara.backend.users.dto.CreateUserResponse;
import com.scenara.backend.users.dto.UpdateUserRequest;
import com.scenara.backend.users.model.UserModel;
import com.scenara.backend.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping()
    public ResponseEntity<CreateUserResponse> criar(@RequestBody CreateUserRequest request) {
        CreateUserResponse userCreate = service.criar(request);

        return ResponseEntity.ok(userCreate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateUserResponse> exibir(@PathVariable String id) {
        CreateUserResponse userShow = service.exibir(id);

        return ResponseEntity.ok(userShow);
    }

    @GetMapping()
    public ResponseEntity<List<CreateUserResponse>> exibir() {
        List<CreateUserResponse> userShows = service.exibirTodos();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userShows);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CreateUserResponse> atualizar(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String id) {
        service.deletar(id);
    }
}
