package com.activeage.api.dto;

import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;

/**
 * DTO que representa a resposta segura do Usuário (sem a senha).
 * Atualizado para enviar os status de validação do CRM para o Frontend.
 */
public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        TipoUsuario tipo,
        StatusValidacao statusValidacao,
        String mensagemValidacao,
        String crm
) {}