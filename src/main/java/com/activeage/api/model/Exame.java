package com.activeage.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "exames")
public class Exame {
    @Id
    private String id;
    private String pacienteId;
    private String nome;
    private String dataRealizacao;
    private String arquivoBase64;
    private String tipoArquivo;
    private LocalDateTime dataUpload;
}