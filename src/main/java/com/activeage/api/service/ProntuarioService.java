package com.activeage.api.service;

import com.activeage.api.dto.ProntuarioRequestDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Prontuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public Prontuario registrarProntuario(String medicoId, ProntuarioRequestDTO dto) {
        Agendamento agenda = agendamentoRepository.findById(dto.agendamentoId())
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        if (!agenda.getMedicoId().equals(medicoId)) {
            throw new RuntimeException("Acesso negado. Apenas o médico responsável pode registrar informações.");
        }

        if (agenda.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new RuntimeException("Este atendimento já foi finalizado e os documentos são imutáveis.");
        }

        Prontuario prontuario = prontuarioRepository.findByAgendamentoId(agenda.getId())
                .orElse(new Prontuario());

        if (prontuario.getId() == null) {
            prontuario.setAgendamentoId(agenda.getId());
            prontuario.setPacienteId(agenda.getPacienteId());
            prontuario.setMedicoId(medicoId);
            prontuario.setHashAssinatura(UUID.randomUUID().toString());
        }

        if (dto.queixaPrincipal() != null) prontuario.setQueixaPrincipal(dto.queixaPrincipal());
        if (dto.diagnostico() != null) prontuario.setDiagnostico(dto.diagnostico());
        if (dto.conduta() != null) prontuario.setConduta(dto.conduta());
        if (dto.receita() != null) prontuario.setReceita(dto.receita());
        if (dto.atestado() != null) prontuario.setAtestado(dto.atestado());
        if (dto.pedidoExames() != null) prontuario.setPedidoExames(dto.pedidoExames());

        prontuario.setDataRegistro(LocalDateTime.now());

        if (dto.finalizar()) {
            prontuario.setImutavel(true);
            agenda.setStatus(StatusAgendamento.CONCLUIDO);
            agendamentoRepository.save(agenda);
        } else {
            prontuario.setImutavel(false);
        }

        return prontuarioRepository.save(prontuario);
    }
}