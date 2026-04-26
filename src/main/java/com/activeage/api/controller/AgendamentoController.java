package com.activeage.api.controller;

import com.activeage.api.dto.AgendamentoRequestDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.model.Agendamento;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AgendamentoRepository agendamentoRepository;

    @PostMapping("/medico/{medicoId}")
    public ResponseEntity<List<Agendamento>> criarHorarios(@PathVariable String medicoId, @RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.ok(agendamentoService.criarHorarios(medicoId, dto));
    }

    @GetMapping("/disponiveis/{medicoId}")
    public ResponseEntity<List<Agendamento>> listarDisponiveis(@PathVariable String medicoId) {
        return ResponseEntity.ok(agendamentoRepository.findByMedicoIdAndStatusAndDataHoraAfterOrderByDataHoraAsc(
                medicoId, StatusAgendamento.DISPONIVEL, LocalDateTime.now()));
    }

    @PutMapping("/marcar/{agendamentoId}/paciente/{pacienteId}")
    public ResponseEntity<Agendamento> marcarConsulta(@PathVariable String agendamentoId, @PathVariable String pacienteId) {
        return ResponseEntity.ok(agendamentoService.agendarConsulta(agendamentoId, pacienteId));
    }

    @PutMapping("/cancelar/{agendamentoId}/usuario/{usuarioId}")
    public ResponseEntity<Agendamento> cancelarConsulta(@PathVariable String agendamentoId, @PathVariable String usuarioId) {
        return ResponseEntity.ok(agendamentoService.cancelarConsulta(agendamentoId, usuarioId));
    }
}