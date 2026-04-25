package com.activeage.api.model;

import com.activeage.api.enums.TipoUsuario;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidade que representa um Usuário no banco de dados MongoDB.
 * A anotação @Document cria uma coleção chamada "usuarios" lá no MongoDB Atlas.
 * A anotação @Data (do Lombok) gera automaticamente os Getters, Setters e Construtores invisivelmente.
 */
@Data
@Document(collection = "usuarios")
public class Usuario {
    @Id // Anotação Responsável por criar o ID no MongoDB
    private String id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private String cpf;
    private String crm;
}