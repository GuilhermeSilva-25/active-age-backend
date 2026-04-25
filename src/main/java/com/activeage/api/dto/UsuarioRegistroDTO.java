package com.activeage.api.dto;

import com.activeage.api.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Record para receber os dados de cadastro vindos do Frontend.
 * Utilizamos anotações de validação para garantir que dados nulos ou vazios
 * não cheguem ao nosso Service.
 */
public record UsuarioRegistroDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O tipo de usuário é obrigatório")
        TipoUsuario tipo,

        String cpf,
        String crm
) {
}