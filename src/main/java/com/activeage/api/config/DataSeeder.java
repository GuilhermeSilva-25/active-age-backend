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

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            long qtdMedicos = usuarioRepository.findAll().stream().filter(u -> u.getTipo() == TipoUsuario.MEDICO).count();

            if (qtdMedicos == 0) {
                System.out.println("🌱 [DATA SEEDER] Semeando 15 médicos fictícios...");
                String senhaPadrao = passwordEncoder.encode("senha123");

                Usuario m1 = criarMedico("Dr. Carlos Mendes", "carlos@med.com", senhaPadrao, "111/SP", "11988887777", "Cardiologia");
                Usuario m2 = criarMedico("Dra. Ana Silveira", "ana@med.com", senhaPadrao, "222/RJ", "21977776666", "Neurologia");
                Usuario m3 = criarMedico("Dr. Roberto Justus", "roberto@med.com", senhaPadrao, "333/MG", "31966665555", "Nutrologia");
                Usuario m4 = criarMedico("Dra. Fernanda Montenegro", "fernanda@med.com", senhaPadrao, "444/RS", "51955554444", "Psiquiatria");
                Usuario m5 = criarMedico("Dr. Drauzio Varella", "drauzio@med.com", senhaPadrao, "555/SP", "11944443333", "Oncologia");
                Usuario m6 = criarMedico("Dra. Silvia Santos", "silvia@med.com", senhaPadrao, "666/SP", "11911112222", "Psiquiatria, Cardiologia");
                Usuario m7 = criarMedico("Dr. Pedro Bial", "pedro@med.com", senhaPadrao, "777/RJ", "21922223333", "");
                Usuario m8 = criarMedico("Dra. Laura Cardoso", "laura@med.com", senhaPadrao, "888/MG", "31933334444", "Ortopedia");
                Usuario m9 = criarMedico("Dr. Gilberto Gil", "gilberto@med.com", senhaPadrao, "999/BA", "21944445555", "Pneumologia");
                Usuario m10 = criarMedico("Dra. Hebe Camargo", "hebe@med.com", senhaPadrao, "101/SP", "11955556666", "Cardiologia, Nutrologia");
                Usuario m11 = criarMedico("Dr. Silvio Santos", "silvios@med.com", senhaPadrao, "112/SP", "11955556666", "Neurologia");
                Usuario m12 = criarMedico("Dra. Rita Lee", "rita@med.com", senhaPadrao, "113/SP", "11955556666", "Reumatologia");
                Usuario m13 = criarMedico("Dr. Faustão", "fausto@med.com", senhaPadrao, "114/SP", "11955556666", "Endocrinologia");
                Usuario m14 = criarMedico("Dra. Xuxa", "xuxa@med.com", senhaPadrao, "115/SP", "11955556666", "");
                Usuario m15 = criarMedico("Dr. Gugu", "gugu@med.com", senhaPadrao, "116/SP", "11955556666", "Cardiologia");

                usuarioRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15));
                System.out.println("✅ [DATA SEEDER] 15 Médicos inseridos com sucesso!");
            }
        };
    }

    private Usuario criarMedico(String nome, String email, String senha, String crm, String telefone, String especializacao) {
        Usuario medico = new Usuario();
        medico.setNome(nome);
        medico.setEmail(email);
        medico.setSenha(senha);
        medico.setTipo(TipoUsuario.MEDICO);
        medico.setCrm(crm);
        medico.setTelefone(telefone);
        medico.setEspecializacao(especializacao.isEmpty() ? "Geriatria" : "Geriatria, " + especializacao);
        medico.setStatusValidacao(StatusValidacao.APROVADO);
        return medico;
    }
}