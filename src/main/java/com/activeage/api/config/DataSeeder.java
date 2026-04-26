package com.activeage.api.config;

import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * Semeador de Dados (Data Seeder).
 * Popula o banco de dados do MongoDB com médicos fictícios automaticamente
 * caso a coleção esteja vazia. Excelente para testes de desenvolvimento.
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            long qtdMedicos = usuarioRepository.findAll().stream()
                    .filter(u -> u.getTipo() == TipoUsuario.MEDICO)
                    .count();

            if (qtdMedicos == 0) {
                System.out.println("🌱 [DATA SEEDER] Banco vazio detectado. Semeando médicos fictícios...");

                String senhaPadrao = passwordEncoder.encode("senha123");

                Usuario m1 = criarMedico("Dr. Carlos Mendes", "carlos@med.com", senhaPadrao, "111222/SP", "11988887777");
                Usuario m2 = criarMedico("Dra. Ana Silveira", "ana@med.com", senhaPadrao, "333444/RJ", "21977776666");
                Usuario m3 = criarMedico("Dr. Roberto Justus", "roberto@med.com", senhaPadrao, "555666/MG", "31966665555");
                Usuario m4 = criarMedico("Dra. Fernanda Montenegro", "fernanda@med.com", senhaPadrao, "777888/RS", "51955554444");
                Usuario m5 = criarMedico("Dr. Drauzio Varella", "drauzio@med.com", senhaPadrao, "999000/SP", "11944443333");

                usuarioRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5));

                System.out.println("✅ [DATA SEEDER] 5 Médicos Aprovados foram inseridos no MongoDB!");
            } else {
                System.out.println("👍 [DATA SEEDER] O banco já possui médicos. Nenhuma ação necessária.");
            }
        };
    }

    /**
     * Função auxiliar para montar o "Ingrediente" (Usuário Médico) rapidamente.
     */
    private Usuario criarMedico(String nome, String email, String senha, String crm, String telefone) {
        Usuario medico = new Usuario();
        medico.setNome(nome);
        medico.setEmail(email);
        medico.setSenha(senha);
        medico.setTipo(TipoUsuario.MEDICO);
        medico.setCrm(crm);
        medico.setTelefone(telefone);
        medico.setStatusValidacao(StatusValidacao.APROVADO);
        return medico;
    }
}