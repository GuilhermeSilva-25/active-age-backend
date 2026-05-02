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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                System.out.println("🌱 [DATA SEEDER] Semeando médicos e lotando agendas...");
                String senhaPadrao = passwordEncoder.encode("senha123");

                Usuario pacienteDummy = new Usuario();
                pacienteDummy.setNome("Roberto Carlos (Paciente Teste)");
                pacienteDummy.setEmail("paciente@teste.com");
                pacienteDummy.setSenha(senhaPadrao);
                pacienteDummy.setTipo(TipoUsuario.PACIENTE);
                pacienteDummy.setCpf("000.000.000-00");
                usuarioRepository.save(pacienteDummy);

                List<Usuario> lendasTech = Arrays.asList(
                        criarMedico("Dr. Linus Torvalds", "linus@med.com", senhaPadrao, "000001/SP", "11900000001", "Cirurgia Vascular"),
                        criarMedico("Dra. Ada Lovelace", "ada@med.com", senhaPadrao, "000002/SP", "11900000002", "Neurologia"),
                        criarMedico("Dr. Alan Turing", "alan@med.com", senhaPadrao, "000003/SP", "11900000003", "Psiquiatria"),
                        criarMedico("Dra. Grace Hopper", "grace@med.com", senhaPadrao, "000004/SP", "11900000004", "Infectologia"),
                        criarMedico("Dr. Dennis Ritchie", "dennis@med.com", senhaPadrao, "000005/SP", "11900000005", "Ortopedia"),
                        criarMedico("Dr. Ken Thompson", "ken@med.com", senhaPadrao, "000006/SP", "11900000006", "Clínica Médica"),
                        criarMedico("Dra. Margaret Hamilton", "margaret@med.com", senhaPadrao, "000007/SP", "11900000007", "Cardiologia"),
                        criarMedico("Dr. Bjarne Stroustrup", "bjarne@med.com", senhaPadrao, "000008/SP", "11900000008", "Gastroenterologia"),
                        criarMedico("Dr. James Gosling", "james@med.com", senhaPadrao, "000009/SP", "11900000009", "Endocrinologia"),
                        criarMedico("Dr. Guido van Rossum", "guido@med.com", senhaPadrao, "000010/SP", "11900000010", "Dermatologia"),
                        criarMedico("Dr. Brendan Eich", "brendan@med.com", senhaPadrao, "000011/SP", "11900000011", "Oftalmologia"),
                        criarMedico("Dr. Tim Berners-Lee", "tim@med.com", senhaPadrao, "000012/SP", "11900000012", "Medicina de Família e Comunidade"),
                        criarMedico("Dr. John Carmack", "john@med.com", senhaPadrao, "000013/SP", "11900000013", "Fisiatria (Medicina de Reabilitação)"),
                        criarMedico("Dr. Anders Hejlsberg", "anders@med.com", senhaPadrao, "000014/SP", "11900000014", "Nefrologia"),
                        criarMedico("Dra. Barbara Liskov", "barbara@med.com", senhaPadrao, "000015/SP", "11900000015", "Pneumologia")
                );

                usuarioRepository.saveAll(lendasTech);

                LocalDateTime dataBase = LocalDateTime.of(2026, 5, 12, 8, 0).truncatedTo(ChronoUnit.MINUTES);
                List<Agendamento> todosAgendamentos = new ArrayList<>();

                for (Usuario medico : lendasTech) {
                    for (int dia = 0; dia < 5; dia++) {
                        for (int hora = 8; hora <= 17; hora++) {
                            if (hora == 12) continue;

                            LocalDateTime horarioSlot = dataBase.plusDays(dia).withHour(hora).withMinute(0);
                            Agendamento slot = criarSlot(medico, horarioSlot);

                            if (Math.random() > 0.3) {
                                slot.setStatus(StatusAgendamento.AGENDADO);
                                slot.setPacienteId(pacienteDummy.getId());
                                slot.setPacienteNome(pacienteDummy.getNome());
                                slot.setPacienteCpf(pacienteDummy.getCpf());
                                slot.setLinkTeleconsulta("https://activeage.me/sala/" + UUID.randomUUID().toString().substring(0, 8));
                            }

                            todosAgendamentos.add(slot);
                        }
                    }
                }

                agendamentoRepository.saveAll(todosAgendamentos);
                System.out.println("✅ [DATA SEEDER] Lendas médicas inseridas com especialidades reais! Agendas lotadas a partir de 12/05/2026!");
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
        medico.setEspecializacao(especializacao);
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