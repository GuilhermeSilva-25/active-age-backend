package com.activeage.api.service;

import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario paciente;
    private Usuario medico;
    private Agendamento agendamentoPago;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(agendamentoService, "paymentServiceUrl", "http://localhost:8081");

        paciente = new Usuario();
        paciente.setId("pac-1");
        paciente.setNome("Paciente Idoso");
        paciente.setTipo(TipoUsuario.PACIENTE);

        medico = new Usuario();
        medico.setId("med-1");
        medico.setNome("Dr. Geriatra");
        medico.setTipo(TipoUsuario.MEDICO);

        agendamentoPago = new Agendamento();
        agendamentoPago.setId("agend-1");
        agendamentoPago.setMedicoId("med-1");
        agendamentoPago.setPacienteId("pac-1");
        agendamentoPago.setValor(new BigDecimal("180.00"));
        agendamentoPago.setValorPago(new BigDecimal("180.00"));
        agendamentoPago.setTransacaoPagamentoId("998877");
        agendamentoPago.setStatus(StatusAgendamento.CONFIRMADO);
        agendamentoPago.setDataHora(LocalDateTime.now().plusDays(3)); // Mais de 24h no futuro
    }

    @Test
    @DisplayName("RN002: Deve bloquear cancelamento com menos de 24h de antecedência pelo paciente")
    void deveLancarExcecaoQuandoPacienteCancelaComMenosDe24h() {
        agendamentoPago.setDataHora(LocalDateTime.now().plusHours(12)); // Menos de 24h

        when(agendamentoRepository.findById("agend-1")).thenReturn(Optional.of(agendamentoPago));
        when(usuarioRepository.findById("pac-1")).thenReturn(Optional.of(paciente));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                agendamentoService.cancelarConsulta("agend-1", "pac-1")
        );

        assertTrue(exception.getMessage().contains("24 horas"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve processar reembolso e reabrir vaga quando paciente cancela com antecedência válida")
    void deveProcessarReembolsoQuandoPacienteCancelaComAntecedenciaValida() {
        when(agendamentoRepository.findById("agend-1")).thenReturn(Optional.of(agendamentoPago));
        when(usuarioRepository.findById("pac-1")).thenReturn(Optional.of(paciente));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento resultado = agendamentoService.cancelarConsulta("agend-1", "pac-1");

        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CANCELADO_PELO_PACIENTE, resultado.getStatus());
        assertEquals("APROVADO", resultado.getStatusReembolso());
        assertNotNull(resultado.getIdReembolso());
        assertEquals(new BigDecimal("180.00"), resultado.getValorReembolsado());
        assertNotNull(resultado.getDataReembolso());

        // Deve salvar a consulta cancelada e também a nova vaga reaberta para o médico
        verify(agendamentoRepository, atLeast(2)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve liberar vaga imediatamente sem estorno quando consulta não foi paga")
    void deveLiberarVagaSemEstornoQuandoConsultaNaoPaga() {
        Agendamento agendamentoPendente = new Agendamento();
        agendamentoPendente.setId("agend-pend");
        agendamentoPendente.setMedicoId("med-1");
        agendamentoPendente.setPacienteId("pac-1");
        agendamentoPendente.setStatus(StatusAgendamento.AGUARDANDO_PAGAMENTO);
        agendamentoPendente.setDataHora(LocalDateTime.now().plusDays(2));

        when(agendamentoRepository.findById("agend-pend")).thenReturn(Optional.of(agendamentoPendente));
        when(usuarioRepository.findById("pac-1")).thenReturn(Optional.of(paciente));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento resultado = agendamentoService.cancelarConsulta("agend-pend", "pac-1");

        assertEquals(StatusAgendamento.DISPONIVEL, resultado.getStatus());
        assertNull(resultado.getPacienteId());
        assertEquals("NAO_APLICAVEL", resultado.getStatusReembolso());
    }

    @Test
    @DisplayName("Deve processar reembolso integral quando o médico cancela uma consulta paga")
    void deveProcessarReembolsoIntegralQuandoMedicoCancelaConsultaPaga() {
        when(agendamentoRepository.findById("agend-1")).thenReturn(Optional.of(agendamentoPago));
        when(usuarioRepository.findById("med-1")).thenReturn(Optional.of(medico));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento resultado = agendamentoService.cancelarConsulta("agend-1", "med-1");

        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CANCELADO_PELO_MEDICO, resultado.getStatus());
        assertEquals("APROVADO", resultado.getStatusReembolso());
        assertNotNull(resultado.getIdReembolso());
        assertEquals(new BigDecimal("180.00"), resultado.getValorReembolsado());
    }
}
