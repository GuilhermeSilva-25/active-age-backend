package com.activeage.api.controller;

import com.activeage.api.dto.ProntuarioRequestDTO;
import com.activeage.api.model.Prontuario;
import com.activeage.api.repository.ProntuarioRepository;
import com.activeage.api.service.ProntuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioService prontuarioService;
    private final ProntuarioRepository prontuarioRepository;

    @PostMapping("/medico/{medicoId}")
    public ResponseEntity<Prontuario> registrar(@PathVariable String medicoId, @RequestBody ProntuarioRequestDTO dto) {
        return ResponseEntity.ok(prontuarioService.registrarProntuario(medicoId, dto));
    }

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<Prontuario> buscarPorAgendamento(@PathVariable String agendamentoId) {
        return prontuarioRepository.findByAgendamentoId(agendamentoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}