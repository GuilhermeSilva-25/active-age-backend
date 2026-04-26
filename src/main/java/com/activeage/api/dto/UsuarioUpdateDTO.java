package com.activeage.api.dto;

public record UsuarioUpdateDTO(
        String nome,
        String telefone,
        String crm,
        String especializacao
) {}