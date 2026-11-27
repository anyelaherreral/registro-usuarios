package com.ufps.registro.controller;

import com.ufps.registro.dto.RegistroDTO;
import com.ufps.registro.dto.RegistroResponse;
import com.ufps.registro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistroResponse> registrar(@Valid @RequestBody RegistroDTO dto) {
        RegistroResponse respuesta = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
