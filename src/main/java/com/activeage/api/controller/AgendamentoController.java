package com.activeage.api.controller;

import com.activeage.api.dto.AgendamentoRequestDTO;
import com.activeage.api.dto.ConfirmarPagamentoDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.model.Agendamento;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<Agendamento>> criarHorarios(@PathVariable String medicoId, @RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.ok(agendamentoService.criarHorarios(medicoId, dto));
    }

    @GetMapping("/disponiveis/{medicoId}")
    public ResponseEntity<List<Agendamento>> listarDisponiveis(@PathVariable String medicoId) {
        return ResponseEntity.ok(agendamentoRepository.findByMedicoIdAndStatusAndDataHoraAfterOrderByDataHoraAsc(
                medicoId, StatusAgendamento.DISPONIVEL, LocalDateTime.now()));
    }

    @PutMapping("/marcar/{agendamentoId}/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Agendamento> marcarConsulta(@PathVariable String agendamentoId, @PathVariable String pacienteId) {
        return ResponseEntity.ok(agendamentoService.agendarConsulta(agendamentoId, pacienteId));
    }

    @PutMapping({
            "/{agendamentoId}/pagamento/confirmar",
            "/{agendamentoId}/confirmar-pagamento"
    })
    public ResponseEntity<Agendamento> confirmarPagamento(
            @PathVariable String agendamentoId,
            @RequestBody(required = false) ConfirmarPagamentoDTO dto
    ) {
        return ResponseEntity.ok(agendamentoService.confirmarPagamento(agendamentoId, dto));
    }

    @PutMapping("/cancelar/{agendamentoId}/usuario/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Agendamento> cancelarConsulta(@PathVariable String agendamentoId, @PathVariable String usuarioId) {
        return ResponseEntity.ok(agendamentoService.cancelarConsulta(agendamentoId, usuarioId));
    }

    public record ReembolsoManualDTO(String motivo) {}

    @PostMapping("/{agendamentoId}/reembolsar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agendamento> reembolsarConsulta(
            @PathVariable String agendamentoId,
            @RequestBody(required = false) ReembolsoManualDTO dto
    ) {
        String motivo = dto != null ? dto.motivo() : "Reembolso solicitado administrativamente";
        return ResponseEntity.ok(agendamentoService.processarReembolsoManual(agendamentoId, motivo));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ADMIN')")
    public ResponseEntity<List<Agendamento>> listarPorPaciente(@PathVariable String pacienteId) {
        return ResponseEntity.ok(agendamentoRepository.findByPacienteIdOrderByDataHoraAsc(pacienteId));
    }

    @GetMapping("/medico/{medicoId}/todos")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<List<Agendamento>> listarPorMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(agendamentoRepository.findByMedicoIdOrderByDataHoraAsc(medicoId));
    }

    public record AvaliacaoDTO(Integer nota, String comentario) {}

    @PutMapping("/{id}/avaliar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Agendamento> avaliarConsulta(@PathVariable String id, @RequestBody AvaliacaoDTO dto) {
        return agendamentoRepository.findById(id).map(agenda -> {
            agenda.setNotaAvaliacao(dto.nota());
            agenda.setComentarioAvaliacao(dto.comentario());
            return ResponseEntity.ok(agendamentoRepository.save(agenda));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medico/{medicoId}/avaliacoes")
    public ResponseEntity<List<Agendamento>> getAvaliacoesDoMedico(@PathVariable String medicoId) {
        return ResponseEntity.ok(agendamentoRepository.findByMedicoIdAndNotaAvaliacaoIsNotNullOrderByDataHoraDesc(medicoId));
    }
}