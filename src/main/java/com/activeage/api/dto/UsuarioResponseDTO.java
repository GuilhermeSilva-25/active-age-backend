package com.activeage.api.dto;

import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        TipoUsuario tipo,
        StatusValidacao statusValidacao,
        String mensagemValidacao,
        String crm,
        String especializacao
) {}