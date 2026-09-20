package com.activeage.api.service;

import com.activeage.api.dto.AgendamentoRequestDTO;
import com.activeage.api.dto.ConfirmarPagamentoDTO;
import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Agendamento> criarHorarios(String medicoId, AgendamentoRequestDTO dto) {
        Usuario medico = usuarioRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("MÃ©dico nÃ£o encontrado"));

        BigDecimal valorConsulta = dto.valor() != null ? dto.valor() :
                (medico.getValorConsulta() != null ? medico.getValorConsulta() : new BigDecimal("180.00"));

        int duracaoConsulta = (dto.duracaoMinutos() != null && dto.duracaoMinutos() > 0) ? dto.duracaoMinutos() :
                (medico.getDuracaoMinutos() != null && medico.getDuracaoMinutos() > 0 ? medico.getDuracaoMinutos() : 45);

        List<Agendamento> horariosExistentes = agendamentoRepository.findByMedicoIdOrderByDataHoraAsc(medicoId)
                .stream()
                .filter(h -> h.getStatus() != StatusAgendamento.CANCELADO_PELO_MEDICO)
                .toList();

        List<Agendamento> novosHorarios = dto.horarios().stream().map(horarioRaw -> {
            LocalDateTime novoHorario = horarioRaw.truncatedTo(ChronoUnit.MINUTES);

            for (Agendamento existente : horariosExistentes) {
                long minutosDiferenca = Math.abs(Duration.between(existente.getDataHora(), novoHorario).toMinutes());
                int duracaoExistente = existente.getDuracaoMinutos() != null ? existente.getDuracaoMinutos() : duracaoConsulta;
                int intervaloNecessario = Math.max(duracaoConsulta, duracaoExistente);

                if (minutosDiferenca < intervaloNecessario) {
                    throw new RuntimeException("Conflito de agenda: O horÃ¡rio " +
                            novoHorario.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) +
                            " Ã© muito prÃ³ximo de outro agendamento (Intervalo necessÃ¡rio: " + intervaloNecessario + " min).");
                }
            }

            Agendamento a = new Agendamento();
            a.setMedicoId(medicoId);
            a.setMedicoNome(medico.getNome());
            a.setMedicoCrm(medico.getCrm());
            a.setMedicoEspecializacao(medico.getEspecializacao());
            a.setDataHora(novoHorario);
            a.setStatus(StatusAgendamento.DISPONIVEL);
            a.setValor(valorConsulta);
            a.setDuracaoMinutos(duracaoConsulta);
            return a;
        }).toList();

        return agendamentoRepository.saveAll(novosHorarios);
    }

    public Agendamento agendarConsulta(String agendamentoId, String pacienteId) {
        Agendamento agenda = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("HorÃ¡rio nÃ£o encontrado"));

        if (agenda.getStatus() != StatusAgendamento.DISPONIVEL) {
            throw new RuntimeException("Este horÃ¡rio nÃ£o estÃ¡ mais disponÃ­vel.");
        }

        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente nÃ£o encontrado"));

        LocalDateTime horaDesejada = agenda.getDataHora().truncatedTo(ChronoUnit.MINUTES);

        List<Agendamento> consultasPaciente = agendamentoRepository.findByPacienteIdOrderByDataHoraAsc(pacienteId);
        boolean jaOcupado = consultasPaciente.stream()
                .anyMatch(c -> (c.getStatus() == StatusAgendamento.AGENDADO || c.getStatus() == StatusAgendamento.CONFIRMADO) &&
                        c.getDataHora().truncatedTo(ChronoUnit.MINUTES).equals(horaDesejada));

        if (jaOcupado) {
            throw new RuntimeException("VocÃª jÃ¡ possui uma consulta agendada para este mesmo horÃ¡rio com outro profissional.");
        }

        agenda.setPacienteId(pacienteId);
        agenda.setPacienteNome(paciente.getNome());
        agenda.setPacienteCpf(paciente.getCpf());

        agenda.setStatus(StatusAgendamento.AGENDADO);
        agenda.setLinkTeleconsulta("https://activeage.me/sala/" + UUID.randomUUID().toString().substring(0, 8));

        return agendamentoRepository.save(agenda);
    }

    public Agendamento confirmarPagamento(String agendamentoId, ConfirmarPagamentoDTO dto) {
        Agendamento agenda = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento nÃ£o encontrado para confirmaÃ§Ã£o de pagamento."));

        agenda.setStatus(StatusAgendamento.CONFIRMADO);
        if (dto != null) {
            if (dto.transacaoId() != null) agenda.setTransacaoPagamentoId(dto.transacaoId());
            if (dto.metodo() != null) agenda.setMetodoPagamento(dto.metodo());
            if (dto.valorPago() != null) agenda.setValor(dto.valorPago());
        }

        return agendamentoRepository.save(agenda);
    }

    public Agendamento cancelarConsulta(String agendamentoId, String usuarioCancelouId) {
        Agendamento agenda = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento nÃ£o encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioCancelouId)
                .orElseThrow(() -> new RuntimeException("UsuÃ¡rio nÃ£o encontrado"));

        if (usuario.getTipo() == TipoUsuario.PACIENTE) {
            agenda.setStatus(StatusAgendamento.DISPONIVEL);
            agenda.setPacienteId(null);
            agenda.setPacienteNome(null);
            agenda.setPacienteCpf(null);
            agenda.setLinkTeleconsulta(null);
            agenda.setTransacaoPagamentoId(null);
            agenda.setMetodoPagamento(null);
        } else {
            agenda.setStatus(StatusAgendamento.CANCELADO_PELO_MEDICO);
        }

        return agendamentoRepository.save(agenda);
    }
}