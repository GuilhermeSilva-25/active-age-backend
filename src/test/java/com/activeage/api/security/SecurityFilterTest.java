package com.activeage.api.security;

import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar com authority ROLE_MEDICO quando token for válido")
    void deveAutenticarMedicoComTokenValido() throws ServletException, IOException {
        String token = "jwt.valido.aqui";
        String email = "medico@clinica.com";

        Usuario medico = new Usuario();
        medico.setId("med-123");
        medico.setEmail(email);
        medico.setTipo(TipoUsuario.MEDICO);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validarToken(token)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(medico));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(medico, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar com authority ROLE_PACIENTE quando token for de paciente")
    void deveAutenticarPacienteComTokenValido() throws ServletException, IOException {
        String token = "jwt.paciente.aqui";
        String email = "paciente@email.com";

        Usuario paciente = new Usuario();
        paciente.setId("pac-456");
        paciente.setEmail(email);
        paciente.setTipo(TipoUsuario.PACIENTE);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validarToken(token)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(paciente));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar com authority ROLE_ADMIN quando token for de administrador")
    void deveAutenticarAdminComTokenValido() throws ServletException, IOException {
        String token = "jwt.admin.aqui";
        String email = "admin@email.com";

        Usuario admin = new Usuario();
        admin.setId("adm-789");
        admin.setEmail(email);
        admin.setTipo(TipoUsuario.ADMIN);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validarToken(token)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando cabeçalho Authorization não for informado")
    void naoDeveAutenticarSemCabecalhoAuthorization() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando token for inválido")
    void naoDeveAutenticarComTokenInvalido() throws ServletException, IOException {
        String token = "invalido";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validarToken(token)).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
