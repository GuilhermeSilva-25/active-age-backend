package com.activeage.api.model;

import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private String cpf;
    private String crm;
    private String telefone;
    private String especializacao;
    private StatusValidacao statusValidacao;
    private String mensagemValidacao;
    private String biografia;
    private boolean assinaturaAtiva = false;

    private BigDecimal valorConsulta;
    private Integer duracaoMinutos;
}