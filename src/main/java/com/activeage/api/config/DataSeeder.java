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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgendamentoRepository agendamentoRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            System.out.println("🌱 [DATA SEEDER] Executando rotina de inicialização e sincronização...");
            sincronizarDadosGerais();
            sincronizarAmbienteDeTesteWebRTC();
            renovarAgendasLendasSeNecessario();
        };
    }

    /**
     * Executa diariamente às 03:00 da madrugada para renovar datas e garantir
     * que sempre haja consultas ativas para testes de teleconsulta.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void rotinaDiariaAtualizacao() {
        System.out.println("⏰ [DATA SEEDER - CRON] Iniciando atualização diária agendada...");
        sincronizarAmbienteDeTesteWebRTC();
        renovarAgendasLendasSeNecessario();
    }

    /**
     * Garante o Administrador e a semeadura das Lendas da Programação caso a base esteja vazia.
     */
    public void sincronizarDadosGerais() {
        long qtdUsuarios = usuarioRepository.count();

        if (qtdUsuarios == 0) {
            System.out.println("🌱 [DATA SEEDER] Semeando Administrador e Médicos Lendas da Programação...");

            String senhaAdmin = passwordEncoder.encode("admin1234");
            Usuario admin = new Usuario();
            admin.setNome("Administrador Active Age");
            admin.setEmail("admin@email.com");
            admin.setSenha(senhaAdmin);
            admin.setTipo(TipoUsuario.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("✅ Administrador criado (admin@email.com / admin1234)");

            String senhaLendas = passwordEncoder.encode("senha123");
            List<Usuario> lendasTech = Arrays.asList(
                    criarMedico("Dr. Linus Torvalds", "linus@med.com", senhaLendas, "000001/SP", "11900000001", "Cirurgia Vascular",
                            "Engenheiro de software finlandês-americano, célebre mundialmente por ter criado o kernel do Linux e o sistema de controle de versão Git."),
                    criarMedico("Dra. Ada Lovelace", "ada@med.com", senhaLendas, "000002/SP", "11900000002", "Neurologia",
                            "Matemática e escritora inglesa do século XIX, reconhecida como a primeira programadora da história."),
                    criarMedico("Dr. Alan Turing", "alan@med.com", senhaLendas, "000003/SP", "11900000003", "Psiquiatria",
                            "Considerado o pai da ciência da computação teórica e da inteligência artificial."),
                    criarMedico("Dra. Grace Hopper", "grace@med.com", senhaLendas, "000004/SP", "11900000004", "Infectologia",
                            "Almirante da Marinha dos EUA e brilhante analista de sistemas. Criadora da base do COBOL."),
                    criarMedico("Dr. Dennis Ritchie", "dennis@med.com", senhaLendas, "000005/SP", "11900000005", "Ortopedia",
                            "Criador da linguagem C e co-criador do sistema operacional Unix."),
                    criarMedico("Dr. Ken Thompson", "ken@med.com", senhaLendas, "000006/SP", "11900000006", "Clínica Médica",
                            "Co-criador do Unix e autor principal da linguagem B. Co-autor do Go."),
                    criarMedico("Dra. Margaret Hamilton", "margaret@med.com", senhaLendas, "000007/SP", "11900000007", "Cardiologia",
                            "Desenvolveu o código de voo responsável pelo pouso da Apollo 11 na Lua."),
                    criarMedico("Dr. Bjarne Stroustrup", "bjarne@med.com", senhaLendas, "000008/SP", "11900000008", "Gastroenterologia",
                            "Inventor e principal impulsionador da linguagem C++."),
                    criarMedico("Dr. James Gosling", "james@med.com", senhaLendas, "000009/SP", "11900000009", "Endocrinologia",
                            "Criador e Pai da linguagem Java."),
                    criarMedico("Dr. Guido van Rossum", "guido@med.com", senhaLendas, "000010/SP", "11900000010", "Dermatologia",
                            "Criador da linguagem de programação Python."),
                    criarMedico("Dr. Brendan Eich", "brendan@med.com", senhaLendas, "000011/SP", "11900000011", "Oftalmologia",
                            "Criador do JavaScript e co-fundador da Mozilla."),
                    criarMedico("Dr. Tim Berners-Lee", "tim@med.com", senhaLendas, "000012/SP", "11900000012", "Medicina de Família",
                            "Inventor da World Wide Web (WWW)."),
                    criarMedico("Dr. John Carmack", "john@med.com", senhaLendas, "000013/SP", "11900000013", "Fisiatria (Reabilitação)",
                            "Co-fundador da id Software, revolucionou a computação gráfica 3D."),
                    criarMedico("Dr. Anders Hejlsberg", "anders@med.com", senhaLendas, "000014/SP", "11900000014", "Nefrologia",
                            "Arquiteto de C#, TypeScript, Turbo Pascal e Delphi."),
                    criarMedico("Dra. Barbara Liskov", "barbara@med.com", senhaLendas, "000015/SP", "11900000015", "Pneumologia",
                            "Ganhadora do Prêmio Turing e criadora do Princípio de Substituição de Liskov.")
            );

            List<Usuario> salvos = usuarioRepository.saveAll(lendasTech);
            gerarSlotsParaMedicos(salvos, LocalDate.now());
        }
    }

    /**
     * Sincroniza o Médico e o Paciente de teste e garante consultas agendadas e já pagas
     * para o dia atual e dias seguintes, permitindo testar a videochamada instantaneamente.
     */
    public void sincronizarAmbienteDeTesteWebRTC() {
        try {
            System.out.println("🔧 [DATA SEEDER] Sincronizando contas de teste e consultas confirmadas...");

            // 1. Garantir Médico de Teste (medico@teste.com / 1234)
            Usuario medico = usuarioRepository.findByEmail("medico@teste.com").orElseGet(() -> {
                Usuario m = new Usuario();
                m.setNome("Dr. Médico de Teste");
                m.setEmail("medico@teste.com");
                m.setTipo(TipoUsuario.MEDICO);
                m.setCrm("999999/SP");
                m.setTelefone("11988887777");
                m.setCpf("999.888.777-66");
                m.setEspecializacao("Clínica Geral & Telemedicina");
                m.setBiografia("Médico credenciado para testes e validação da teleconsulta em tempo real.");
                return m;
            });

            medico.setSenha(passwordEncoder.encode("1234"));
            medico.setStatusValidacao(StatusValidacao.APROVADO);
            medico.setAssinaturaAtiva(true);
            medico.setValorConsulta(new BigDecimal("150.00"));
            medico.setDuracaoMinutos(40);
            medico = usuarioRepository.save(medico);

            // 2. Garantir Paciente de Teste (paciente@teste.com / 1234)
            Usuario paciente = usuarioRepository.findByEmail("paciente@teste.com").orElseGet(() -> {
                Usuario p = new Usuario();
                p.setNome("Paciente de Teste");
                p.setEmail("paciente@teste.com");
                p.setTipo(TipoUsuario.PACIENTE);
                p.setCpf("111.222.333-44");
                p.setTelefone("11999998888");
                return p;
            });

            paciente.setSenha(passwordEncoder.encode("1234"));
            paciente = usuarioRepository.save(paciente);

            // 3. Garantir consultas pagas e confirmadas para HOJE e próximos dias
            List<Agendamento> consultasExistentes = agendamentoRepository.findByMedicoIdOrderByDataHoraAsc(medico.getId());

            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            List<LocalDateTime> horariosAlvo = new ArrayList<>();

            // A) Horários para HOJE:
            // 1) Consulta Imediata (para testar agora mesmo - daqui a 5 minutos)
            horariosAlvo.add(now.plusMinutes(5));
            // 2) Mais consultas ao longo das próximas horas
            if (now.getHour() <= 18) {
                horariosAlvo.add(now.plusHours(2).withMinute(0));
                horariosAlvo.add(now.plusHours(4).withMinute(0));
            } else {
                horariosAlvo.add(now.plusHours(1).withMinute(30));
            }

            // B) Horários para AMANHÃ (D+1):
            LocalDate amanha = LocalDate.now().plusDays(1);
            horariosAlvo.add(amanha.atTime(9, 0));
            horariosAlvo.add(amanha.atTime(11, 0));
            horariosAlvo.add(amanha.atTime(14, 0));
            horariosAlvo.add(amanha.atTime(16, 0));

            // C) Horários para DEPOIS DE AMANHÃ (D+2):
            LocalDate depoisAmanha = LocalDate.now().plusDays(2);
            horariosAlvo.add(depoisAmanha.atTime(10, 0));
            horariosAlvo.add(depoisAmanha.atTime(15, 0));

            List<Agendamento> novasConsultas = new ArrayList<>();

            for (LocalDateTime horario : horariosAlvo) {
                boolean jaExiste = consultasExistentes.stream().anyMatch(a ->
                        a.getDataHora() != null &&
                        Math.abs(Duration.between(a.getDataHora(), horario).toMinutes()) < 35
                );

                if (!jaExiste) {
                    Agendamento consulta = new Agendamento();
                    consulta.setMedicoId(medico.getId());
                    consulta.setMedicoNome(medico.getNome());
                    consulta.setMedicoCrm(medico.getCrm());
                    consulta.setMedicoEspecializacao(medico.getEspecializacao());

                    consulta.setPacienteId(paciente.getId());
                    consulta.setPacienteNome(paciente.getNome());
                    consulta.setPacienteCpf(paciente.getCpf());

                    consulta.setDataHora(horario);
                    consulta.setStatus(StatusAgendamento.CONFIRMADO);
                    consulta.setDuracaoMinutos(40);
                    consulta.setValor(new BigDecimal("150.00"));
                    consulta.setValorPago(new BigDecimal("150.00"));
                    consulta.setDataPagamento(now);
                    consulta.setMetodoPagamento("PIX");
                    consulta.setTransacaoPagamentoId("TEST-PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    consulta.setLinkTeleconsulta("https://active-age-frontend.vercel.app/sala/" + UUID.randomUUID().toString().substring(0, 8));

                    novasConsultas.add(consulta);
                }
            }

            if (!novasConsultas.isEmpty()) {
                agendamentoRepository.saveAll(novasConsultas);
                System.out.println("✅ [DATA SEEDER] " + novasConsultas.size() + " consultas confirmadas/pagas inseridas para teste de vídeo!");
            } else {
                System.out.println("ℹ️ [DATA SEEDER] Consultas de teste para hoje e próximos dias já estão atualizadas.");
            }

            System.out.println("✅ [DATA SEEDER] Usuários de teste prontos:");
            System.out.println("   👉 Médico: medico@teste.com | Senha: 1234 (CRM Aprovado & Assinatura Ativa)");
            System.out.println("   👉 Paciente: paciente@teste.com | Senha: 1234 (Consultas Confirmadas)");

        } catch (Exception e) {
            System.err.println("❌ [DATA SEEDER] Erro ao sincronizar ambiente de teste: " + e.getMessage());
        }
    }

    /**
     * Verifica se os médicos já cadastrados possuem horários no futuro e, caso contrário,
     * renova a grade de horários disponíveis.
     */
    public void renovarAgendasLendasSeNecessario() {
        try {
            List<Usuario> medicos = usuarioRepository.findByTipoAndStatusValidacao(TipoUsuario.MEDICO, StatusValidacao.APROVADO);
            for (Usuario medico : medicos) {
                if ("medico@teste.com".equals(medico.getEmail())) continue;

                List<Agendamento> futuros = agendamentoRepository.findByMedicoIdAndStatusAndDataHoraAfterOrderByDataHoraAsc(
                        medico.getId(), StatusAgendamento.DISPONIVEL, LocalDateTime.now()
                );

                if (futuros.isEmpty()) {
                    gerarSlotsParaMedicos(Collections.singletonList(medico), LocalDate.now());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [DATA SEEDER] Erro ao renovar agendas: " + e.getMessage());
        }
    }

    private void gerarSlotsParaMedicos(List<Usuario> medicos, LocalDate dataInicio) {
        List<Agendamento> slotsParaSalvar = new ArrayList<>();
        for (Usuario medico : medicos) {
            for (int dia = 0; dia < 4; dia++) {
                LocalDate diaConsulta = dataInicio.plusDays(dia);
                for (int hora = 9; hora <= 17; hora += 2) {
                    LocalDateTime dataHora = diaConsulta.atTime(hora, 0);
                    if (dataHora.isAfter(LocalDateTime.now())) {
                        Agendamento slot = criarSlot(medico, dataHora);
                        slotsParaSalvar.add(slot);
                    }
                }
            }
        }
        if (!slotsParaSalvar.isEmpty()) {
            agendamentoRepository.saveAll(slotsParaSalvar);
        }
    }

    private Usuario criarMedico(String nome, String email, String senha, String crm, String telefone, String especializacao, String biografia) {
        Usuario medico = new Usuario();
        medico.setNome(nome);
        medico.setEmail(email);
        medico.setSenha(senha);
        medico.setTipo(TipoUsuario.MEDICO);
        medico.setCrm(crm);
        medico.setTelefone(telefone);
        medico.setEspecializacao(especializacao);
        medico.setBiografia(biografia);
        medico.setStatusValidacao(StatusValidacao.APROVADO);
        medico.setAssinaturaAtiva(true);

        long randomValor = Math.round(100 + Math.random() * 250);
        medico.setValorConsulta(BigDecimal.valueOf(randomValor));
        medico.setDuracaoMinutos(40);

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

        a.setValor(medico.getValorConsulta() != null ? medico.getValorConsulta() : new BigDecimal("180.00"));
        a.setDuracaoMinutos(medico.getDuracaoMinutos() != null ? medico.getDuracaoMinutos() : 40);

        return a;
    }
}
