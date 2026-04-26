package com.activeage.api.dto;

import com.activeage.api.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRegistroDTO(
        @NotBlank(message = "O nome é obrigatório") String nome,
        @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido") String email,
        @NotBlank(message = "A senha é obrigatória") String senha,
        @NotNull(message = "O tipo de usuário é obrigatório") TipoUsuario tipo,
        String cpf,
        String crm,
        String telefone,
        String especializacao
) {}