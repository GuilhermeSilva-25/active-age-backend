package com.activeage.api.controller;

import com.activeage.api.dto.ValidacaoRequestDTO;
import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/validacoes")
@RequiredArgsConstructor
public class ValidacaoController {

    private final UsuarioRepository usuarioRepository;

    // 1. Médico solicita validação
    @PostMapping("/solicitar/{id}")
    public ResponseEntity<Usuario> solicitarValidacao(@PathVariable String id) {
        Usuario medico = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medico.setStatusValidacao(StatusValidacao.EM_ANALISE);
        return ResponseEntity.ok(usuarioRepository.save(medico));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Usuario>> listarPendentes() {
        // Busca todos e filtra apenas os Médicos que estão EM_ANALISE
        List<Usuario> pendentes = usuarioRepository.findAll().stream()
                .filter(u -> u.getTipo() == TipoUsuario.MEDICO && u.getStatusValidacao() == StatusValidacao.EM_ANALISE)
                .toList();
        return ResponseEntity.ok(pendentes);
    }

    @PutMapping("/avaliar/{id}")
    public ResponseEntity<Usuario> avaliarMedico(@PathVariable String id, @RequestBody ValidacaoRequestDTO dto) {
        Usuario medico = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medico.setStatusValidacao(dto.status());
        medico.setMensagemValidacao(dto.mensagem());
        return ResponseEntity.ok(usuarioRepository.save(medico));
    }
}