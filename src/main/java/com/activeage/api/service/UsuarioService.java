package com.activeage.api.service;

import com.activeage.api.dto.UsuarioRegistroDTO;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Classe responsável pelas regras de negócio relacionadas ao Usuário.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Recebe os dados de registro, valida se o email existe e salva no MongoDB.
     */
    public Usuario cadastrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setTipo(dto.tipo());
        novoUsuario.setCpf(dto.cpf());
        novoUsuario.setCrm(dto.crm());

        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));

        return usuarioRepository.save(novoUsuario);
    }
}