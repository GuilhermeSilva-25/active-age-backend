package com.activeage.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "prontuarios")
public class Prontuario {
    @Id
    private String id;
    private String agendamentoId;
    private String pacienteId;
    private String medicoId;

    private String queixaPrincipal;
    private String diagnostico;
    private String conduta;

    private boolean imutavel;
    private LocalDateTime dataRegistro;
}