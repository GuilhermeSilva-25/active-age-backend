package com.activeage.api.controller;

import com.activeage.api.dto.UsuarioRegistroDTO;
import com.activeage.api.dto.UsuarioUpdateDTO;
import com.activeage.api.model.Usuario;
import com.activeage.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid UsuarioRegistroDTO dto) {
        Usuario usuarioCriado = usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<Usuario> atualizarPerfil(@PathVariable String id, @RequestBody UsuarioUpdateDTO dto) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizarPerfil(id, dto);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}