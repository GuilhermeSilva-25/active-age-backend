package com.activeage.api.controller;

import com.activeage.api.dto.UsuarioRegistroDTO;
import com.activeage.api.dto.UsuarioResponseDTO;
import com.activeage.api.dto.UsuarioUpdateDTO;
import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import com.activeage.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

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

    @GetMapping("/medicos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarMedicosAprovados() {
        List<Usuario> medicos = usuarioRepository.findByTipoAndStatusValidacao(TipoUsuario.MEDICO, StatusValidacao.APROVADO);

        List<UsuarioResponseDTO> medicosSeguros = medicos.stream().map(m -> new UsuarioResponseDTO(
                m.getId(), m.getNome(), m.getEmail(), m.getTipo(), m.getStatusValidacao(), m.getMensagemValidacao(), m.getCrm()
        )).toList();

        return ResponseEntity.ok(medicosSeguros);
    }
}