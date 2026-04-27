package com.activeage.api.service;

import com.activeage.api.dto.ProntuarioRequestDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Prontuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final AgendamentoRepository agendamentoRepository;

    public Prontuario registrarProntuario(String medicoId, ProntuarioRequestDTO dto) {
        Agendamento agenda = agendamentoRepository.findById(dto.agendamentoId())
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        if (!agenda.getMedicoId().equals(medicoId)) {
            throw new RuntimeException("Acesso negado. Apenas o médico responsável pode registrar o prontuário.");
        }

        if (agenda.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new RuntimeException("Este atendimento já possui um prontuário finalizado e imutável.");
        }

        Prontuario prontuario = new Prontuario();
        prontuario.setAgendamentoId(agenda.getId());
        prontuario.setPacienteId(agenda.getPacienteId());
        prontuario.setMedicoId(medicoId);
        prontuario.setQueixaPrincipal(dto.queixaPrincipal());
        prontuario.setDiagnostico(dto.diagnostico());
        prontuario.setConduta(dto.conduta());
        prontuario.setDataRegistro(LocalDateTime.now());

        prontuario.setImutavel(true);
        Prontuario prontuarioSalvo = prontuarioRepository.save(prontuario);

        agenda.setStatus(StatusAgendamento.CONCLUIDO);
        agendamentoRepository.save(agenda);

        return prontuarioSalvo;
    }
}