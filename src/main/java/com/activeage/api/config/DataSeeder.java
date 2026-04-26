package com.activeage.api.config;

import com.activeage.api.enums.StatusAgendamento;
import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Agendamento;
import com.activeage.api.model.Usuario;
import com.activeage.api.repository.AgendamentoRepository;
import com.activeage.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgendamentoRepository agendamentoRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            long qtdMedicos = usuarioRepository.findAll().stream().filter(u -> u.getTipo() == TipoUsuario.MEDICO).count();

            if (qtdMedicos == 0) {
                System.out.println("🌱 [DATA SEEDER] Semeando médicos e agendas...");
                String senhaPadrao = passwordEncoder.encode("senha123");

                Usuario m1 = criarMedico("Dr. Carlos Mendes", "carlos@med.com", senhaPadrao, "111222/SP", "11988887777", "Cardiologia");
                Usuario m2 = criarMedico("Dra. Ana Silveira", "ana@med.com", senhaPadrao, "333444/RJ", "21977776666", "Neurologia");
                Usuario m3 = criarMedico("Dr. Pedro Bial", "pedro@med.com", senhaPadrao, "555666/RJ", "21922223333", "Sem Agenda");

                usuarioRepository.saveAll(Arrays.asList(m1, m2, m3));

                LocalDateTime amanha = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).truncatedTo(ChronoUnit.MINUTES);

                Agendamento a1 = criarSlot(m1, amanha);
                Agendamento a2 = criarSlot(m1, amanha.plusHours(1));
                Agendamento a3 = criarSlot(m2, amanha.plusHours(2));

                agendamentoRepository.saveAll(Arrays.asList(a1, a2, a3));
                System.out.println("✅ [DATA SEEDER] Médicos e Agendas inseridos (Agora com CRM e Especialidade corretos)!");
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

    private Agendamento criarSlot(Usuario medico, LocalDateTime dataHora) {
        Agendamento a = new Agendamento();
        a.setMedicoId(medico.getId());
        a.setMedicoNome(medico.getNome());
        a.setMedicoCrm(medico.getCrm());
        a.setMedicoEspecializacao(medico.getEspecializacao());
        a.setDataHora(dataHora);
        a.setStatus(StatusAgendamento.DISPONIVEL);
        return a;
    }
}