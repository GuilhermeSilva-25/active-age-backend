package com.activeage.api.model;

import com.activeage.api.enums.StatusAgendamento;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "agendamentos")
public class Agendamento {
    @Id
    private String id;
    private String medicoId;
    private String pacienteId;
    private LocalDateTime dataHora;
    private StatusAgendamento status;
    private String linkTeleconsulta;
}