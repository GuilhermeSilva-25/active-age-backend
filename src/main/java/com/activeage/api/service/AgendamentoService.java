package com.activeage.api.service;

import com.activeage.api.dto.AgendamentoRequestDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Agendamento> criarHorarios(String medicoId, AgendamentoRequestDTO dto) {
        Usuario medico = usuarioRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        List<Agendamento> novosHorarios = dto.horarios().stream().map(horario -> {
            Agendamento a = new Agendamento();
            a.setMedicoId(medicoId);
            a.setMedicoNome(medico.getNome());
            a.setMedicoCrm(medico.getCrm());
            a.setMedicoEspecializacao(medico.getEspecializacao());
            a.setDataHora(horario);
            a.setStatus(StatusAgendamento.DISPONIVEL);
            return a;
        }).toList();

        return agendamentoRepository.saveAll(novosHorarios);
    }

    public Agendamento agendarConsulta(String agendamentoId, String pacienteId) {
        Agendamento agenda = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Horário não encontrado"));

        if (agenda.getStatus() != StatusAgendamento.DISPONIVEL) {
            throw new RuntimeException("Este horário não está mais disponível.");
        }

        List<Agendamento> conflitos = agendamentoRepository.findByPacienteIdOrderByDataHoraAsc(pacienteId);
        boolean jaOcupado = conflitos.stream()
                .anyMatch(c -> c.getStatus() == StatusAgendamento.AGENDADO && c.getDataHora().equals(agenda.getDataHora()));

        if (jaOcupado) {
            throw new RuntimeException("Você já possui uma consulta agendada para este mesmo horário com outro profissional.");
        }

        agenda.setPacienteId(pacienteId);
        agenda.setStatus(StatusAgendamento.AGENDADO);
        agenda.setLinkTeleconsulta("https://activeage.me/sala/" + UUID.randomUUID().toString().substring(0, 8));

        return agendamentoRepository.save(agenda);
    }

    public Agendamento cancelarConsulta(String agendamentoId, String usuarioCancelouId) {
        Agendamento agenda = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioCancelouId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getTipo() == TipoUsuario.PACIENTE) {
            agenda.setStatus(StatusAgendamento.DISPONIVEL);
            agenda.setPacienteId(null);
            agenda.setLinkTeleconsulta(null);
        } else {
            agenda.setStatus(StatusAgendamento.CANCELADO_PELO_MEDICO);
        }

        return agendamentoRepository.save(agenda);
    }
}