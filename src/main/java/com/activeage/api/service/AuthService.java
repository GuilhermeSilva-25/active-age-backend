package com.activeage.api.service;

import com.activeage.api.dto.LoginDTO;
import com.activeage.api.dto.LoginResponseDTO;
import com.activeage.api.dto.UsuarioResponseDTO;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import com.activeage.api.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Valida as credenciais e gera o token de acesso.
     */
    public LoginResponseDTO realizarLogin(LoginDTO loginDTO) {

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado"));

        if (!passwordEncoder.matches(loginDTO.senha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = tokenService.gerarToken(usuario);

        UsuarioResponseDTO usuarioSeguro = new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo(),
                usuario.getStatusValidacao(),
                usuario.getMensagemValidacao(),
                usuario.getCrm()
        );

        return new LoginResponseDTO(token, usuarioSeguro);
    }
}