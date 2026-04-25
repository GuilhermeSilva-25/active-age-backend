package com.activeage.api.controller;

import com.activeage.api.dto.UsuarioRegistroDTO;
import com.activeage.api.model.Usuario;
import com.activeage.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expõe os endpoints de Usuário para a internet.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint para criação de um novo usuário.
     * O @RequestBody transforma o JSON enviado pelo Frontend no nosso UsuarioRegistroDTO.
     * O @Valid ativa as validações (como @NotBlank e @Email) definidas no DTO.
     */
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid UsuarioRegistroDTO dto) {
        Usuario usuarioCriado = usuarioService.cadastrarUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }
}