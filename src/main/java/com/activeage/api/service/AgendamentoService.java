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
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

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
                    throw new RuntimeException("Conflito de agenda: O horário " +
                            novoHorario.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) +
                            " é muito próximo de outro agendamento (Intervalo necessário: " + intervaloNecessario + " min).");
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
                .orElseThrow(() -> new RuntimeException("Horário não encontrado"));

        if (agenda.getStatus() != StatusAgendamento.DISPONIVEL) {
            throw new RuntimeException("Este horário não está mais disponível.");
        }

        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        LocalDateTime horaDesejada = agenda.getDataHora().truncatedTo(ChronoUnit.MINUTES);

        List<Agendamento> consultasPaciente = agendamentoRepository.findByPacienteIdOrderByDataHoraAsc(pacienteId);
        boolean jaOcupado = consultasPaciente.stream()
                .anyMatch(c -> (c.getStatus() == StatusAgendamento.AGENDADO || c.getStatus() == StatusAgendamento.CONFIRMADO || c.getStatus() == StatusAgendamento.AGUARDANDO_PAGAMENTO) &&
                        c.getDataHora().truncatedTo(ChronoUnit.MINUTES).equals(horaDesejada));

        if (jaOcupado) {
            throw new RuntimeException("Você já possui uma consulta agendada para este mesmo horário com outro profissional.");
        }

        agenda.setPacienteId(pacienteId);
        agenda.setPacienteNome(paciente.getNome());
        agenda.setPacienteCpf(paciente.getCpf());

        agenda.setStatus(StatusAgendamento.AGUARDANDO_PAGAMENTO);
        agenda.setDataBloqueioVaga(LocalDateTime.now());
        agenda.setLinkTeleconsulta(null);
        agenda.setValorPago(null);
        agenda.setDataPagamento(null);

        return agendamentoRepository.save(agenda);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void limparVagasNaoPagas() {
        LocalDateTime tempoLimite = LocalDateTime.now().minusMinutes(15);
        List<Agendamento> aguardando = agendamentoRepository.findByStatus(StatusAgendamento.AGUARDANDO_PAGAMENTO);
        for (Agendamento agenda : aguardando) {
            if (agenda.getDataBloqueioVaga() != null && agenda.getDataBloqueioVaga().isBefore(tempoLimite)) {
                agenda.setPacienteId(null);
                agenda.setPacienteNome(null);
                agenda.setPacienteCpf(null);
                agenda.setStatus(StatusAgendamento.DISPONIVEL);
                agenda.setDataBloqueioVaga(null);
                agendamentoRepository.save(agenda);
                System.out.println("Vaga liberada por falta de pagamento (15 min excedidos): " + agenda.getId());
            }
        }
    }

    public Agendamento confirmarPagamento(String agendamentoId, ConfirmarPagamentoDTO dto) {
        String idLimpo = agendamentoId != null && agendamentoId.startsWith("AGEND-")
                ? agendamentoId.substring(6)
                : agendamentoId;

        Agendamento agenda = agendamentoRepository.findById(idLimpo)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado para confirmação de pagamento."));

        if (agenda.getStatus() == StatusAgendamento.CANCELADO_PELO_MEDICO) {
            throw new RuntimeException("Não é possível confirmar pagamento de uma consulta cancelada pelo médico.");
        }

        BigDecimal valorEsperado = agenda.getValor() != null ? agenda.getValor() : BigDecimal.ZERO;
        BigDecimal valorRecebido = (dto != null && dto.valorPago() != null) ? dto.valorPago() : valorEsperado;

        if (dto != null && dto.valorPago() != null && valorEsperado.compareTo(BigDecimal.ZERO) > 0) {
            if (dto.valorPago().compareTo(valorEsperado) < 0) {
                throw new RuntimeException("Valor pago insuficiente: R$ " + dto.valorPago() + " (esperado: R$ " + valorEsperado + ")");
            }
        }

        LocalDateTime dataHoraPagamento = (dto != null && dto.dataPagamento() != null)
                ? dto.dataPagamento()
                : LocalDateTime.now();

        agenda.setValorPago(valorRecebido);
        agenda.setDataPagamento(dataHoraPagamento);

        if (dto != null) {
            if (dto.transacaoId() != null) agenda.setTransacaoPagamentoId(dto.transacaoId());
            if (dto.metodo() != null) agenda.setMetodoPagamento(dto.metodo());
        }

        agenda.setStatus(StatusAgendamento.CONFIRMADO);
        if (agenda.getLinkTeleconsulta() == null || agenda.getLinkTeleconsulta().isBlank()) {
            agenda.setLinkTeleconsulta("https://activeage.me/sala/" + UUID.randomUUID().toString().substring(0, 8));
        }

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
            agenda.setPacienteNome(null);
            agenda.setPacienteCpf(null);
            agenda.setLinkTeleconsulta(null);
            agenda.setTransacaoPagamentoId(null);
            agenda.setMetodoPagamento(null);
            agenda.setValorPago(null);
            agenda.setDataPagamento(null);
        } else {
            agenda.setStatus(StatusAgendamento.CANCELADO_PELO_MEDICO);
        }

        return agendamentoRepository.save(agenda);
    }
}