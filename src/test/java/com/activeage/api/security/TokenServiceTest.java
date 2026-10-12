package com.activeage.api.security;

import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "teste-secret-key-123456");
    }

    @Test
    @DisplayName("Deve gerar token e validar com sucesso retornando o subject/email")
    void deveGerarEValidarTokenComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId("user-1");
        usuario.setEmail("medico@teste.com");
        usuario.setTipo(TipoUsuario.MEDICO);

        String token = tokenService.gerarToken(usuario);
        assertNotNull(token);
        assertFalse(token.isBlank());

        String subject = tokenService.validarToken(token);
        assertEquals("medico@teste.com", subject);
    }

    @Test
    @DisplayName("Deve retornar null ao validar token inválido")
    void deveRetornarNullParaTokenInvalido() {
        String resultado = tokenService.validarToken("token_invalido_qualquer");
        assertNull(resultado);
    }
}
