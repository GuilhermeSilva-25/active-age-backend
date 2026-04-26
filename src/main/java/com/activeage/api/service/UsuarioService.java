package com.activeage.api.service;

import com.activeage.api.dto.UsuarioRegistroDTO;
import com.activeage.api.dto.UsuarioUpdateDTO;
import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
        novoUsuario.setTelefone(dto.telefone());
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));

        if (dto.tipo() == TipoUsuario.MEDICO) {
            novoUsuario.setStatusValidacao(StatusValidacao.PENDENTE);
            novoUsuario.setEspecializacao(formatarEspecializacao(dto.especializacao()));
        }

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario atualizarPerfil(String id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.nome() != null && !dto.nome().isBlank()) usuario.setNome(dto.nome());
        if (dto.telefone() != null) usuario.setTelefone(dto.telefone());

        if (usuario.getTipo() == TipoUsuario.MEDICO) {
            if (dto.crm() != null && !dto.crm().equals(usuario.getCrm())) {
                if (usuario.getStatusValidacao() == StatusValidacao.APROVADO || usuario.getStatusValidacao() == StatusValidacao.EM_ANALISE) {
                    throw new RuntimeException("O CRM não pode ser alterado durante ou após a análise.");
                }
                usuario.setCrm(dto.crm());
                if (usuario.getStatusValidacao() == StatusValidacao.REPROVADO) {
                    usuario.setStatusValidacao(StatusValidacao.PENDENTE);
                    usuario.setMensagemValidacao(null);
                }
            }

            if (dto.especializacao() != null) {
                usuario.setEspecializacao(formatarEspecializacao(dto.especializacao()));
            }
        }
        return usuarioRepository.save(usuario);
    }

    private String formatarEspecializacao(String recebida) {
        if (recebida == null || recebida.isBlank()) return "Geriatria";

        String extrasLimpos = Arrays.stream(recebida.split(","))
                .map(String::trim)
                .filter(s -> !s.equalsIgnoreCase("Geriatria") && !s.isEmpty())
                .collect(Collectors.joining(", "));

        return extrasLimpos.isEmpty() ? "Geriatria" : "Geriatria, " + extrasLimpos;
    }
}