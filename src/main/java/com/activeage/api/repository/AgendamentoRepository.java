package com.activeage.api.repository;

import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.model.Agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
    List<Agendamento> findByMedicoIdAndStatusAndDataHoraAfterOrderByDataHoraAsc(String medicoId, StatusAgendamento status, LocalDateTime dataHora);
    List<Agendamento> findByMedicoIdOrderByDataHoraAsc(String medicoId);
    List<Agendamento> findByPacienteIdOrderByDataHoraAsc(String pacienteId);
    List<Agendamento> findByMedicoIdAndNotaAvaliacaoIsNotNullOrderByDataHoraDesc(String medicoId);
}