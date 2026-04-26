package com.activeage.api.dto;

/**
 * DTO que carrega apenas os dados permitidos para atualização no Perfil.
 */
public record UsuarioUpdateDTO(
        String nome,
        String telefone,
        String crm
) {
}