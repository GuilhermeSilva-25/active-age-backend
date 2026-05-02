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
                System.out.println("🌱 [DATA SEEDER] Semeando Administrador, Médicos e lotando agendas...");
                String senhaPadrao = passwordEncoder.encode("senha123");

                Usuario admin = new Usuario();
                admin.setNome("Administrador Active Age");
                admin.setEmail("admin@email.com");
                admin.setSenha(passwordEncoder.encode("admin1234"));
                admin.setTipo(TipoUsuario.ADMIN);
                usuarioRepository.save(admin);
                System.out.println("👑 [DATA SEEDER] Admin criado: admin@email.com / admin1234");

                Usuario pacienteDummy = new Usuario();
                pacienteDummy.setNome("Sr. Bug (Paciente Teste)");
                pacienteDummy.setEmail("paciente@teste.com");
                pacienteDummy.setSenha(senhaPadrao);
                pacienteDummy.setTipo(TipoUsuario.PACIENTE);
                pacienteDummy.setCpf("000.000.000-00");
                usuarioRepository.save(pacienteDummy);

                List<Usuario> lendasTech = Arrays.asList(
                        criarMedico("Dr. Linus Torvalds", "linus@med.com", senhaPadrao, "000001/SP", "11900000001", "Cirurgia Vascular", "Especialista no mapeamento de fluxos e ramificações (branches) do sistema circulatório. Defensor de protocolos médicos de código aberto e colaborativos."),
                        criarMedico("Dra. Ada Lovelace", "ada@med.com", senhaPadrao, "000002/SP", "11900000002", "Neurologia", "Pioneira na lógica neurológica. Reconhecida mundialmente por prever o potencial de algoritmos complexos no tratamento de distúrbios cognitivos no primeiro século da medicina analítica."),
                        criarMedico("Dr. Alan Turing", "alan@med.com", senhaPadrao, "000003/SP", "11900000003", "Psiquiatria", "Dedicado à decodificação da mente humana. Especialista na resolução de enigmas comportamentais complexos e pioneiro no teste que avalia a consciência artificial e humana."),
                        criarMedico("Dra. Grace Hopper", "grace@med.com", senhaPadrao, "000004/SP", "11900000004", "Infectologia", "Especialista com vasta experiência em depurar infecções do sistema. Famosa por ter encontrado o primeiro 'bug' biológico literal em um laboratório naval e erradicá-lo com protocolos estritos."),
                        criarMedico("Dr. Dennis Ritchie", "dennis@med.com", senhaPadrao, "000005/SP", "11900000005", "Ortopedia", "Ortopedista focado em estruturar as bases fundamentais do corpo. Suas técnicas pioneiras (linguagem C) servem de alicerce para praticamente todos os tratamentos modernos."),
                        criarMedico("Dr. Ken Thompson", "ken@med.com", senhaPadrao, "000006/SP", "11900000006", "Clínica Médica", "Clínico geral com abordagem minimalista e eficiente. Co-criador de protocolos vitais duradouros que formam o núcleo de clínicas modernas de alta estabilidade."),
                        criarMedico("Dra. Margaret Hamilton", "margaret@med.com", senhaPadrao, "000007/SP", "11900000007", "Cardiologia", "Cardiologista com precisão espacial. Sua engenharia de software biomédico garantiu suporte vital contínuo, impedindo falhas críticas no coração sob extrema pressão atmosférica."),
                        criarMedico("Dr. Bjarne Stroustrup", "bjarne@med.com", senhaPadrao, "000008/SP", "11900000008", "Gastroenterologia", "Focado em tratamentos complexos orientados a objetos. Abordagem altamente estruturada (++), melhorando o legado dos tratamentos clássicos com maior robustez e controle."),
                        criarMedico("Dr. James Gosling", "james@med.com", senhaPadrao, "000009/SP", "11900000009", "Endocrinologia", "Acredita no lema: 'Escreva um tratamento uma vez, e o corpo funcionará em qualquer lugar'. Especialista na máquina virtual que equilibra o sistema hormonal do corpo humano."),
                        criarMedico("Dr. Guido van Rossum", "guido@med.com", senhaPadrao, "000010/SP", "11900000010", "Dermatologia", "Valoriza a elegância, a simplicidade e a clareza na epiderme. Exige tratamentos com perfeita 'indentação', provando que uma pele limpa é sinal de saúde e boa sintaxe."),
                        criarMedico("Dr. Brendan Eich", "brendan@med.com", senhaPadrao, "000011/SP", "11900000011", "Oftalmologia", "Dinâmico e ágil. Famoso no mundo médico por ter criado uma terapia visual completa e funcional em apenas 10 dias, que até hoje é utilizada por 99% das clínicas do globo."),
                        criarMedico("Dr. Tim Berners-Lee", "tim@med.com", senhaPadrao, "000012/SP", "11900000012", "Medicina de Família e Comunidade", "Especialista em conectar a grande teia humana. Foi pioneiro em criar links e pontes de saúde comunitária, interligando a saúde primária mundialmente (WWW)."),
                        criarMedico("Dr. John Carmack", "john@med.com", senhaPadrao, "000013/SP", "11900000013", "Fisiatria (Medicina de Reabilitação)", "Fisiatra revolucionário. Aplica tecnologias de renderização 3D e motores virtuais ultrarrápidos (como os usados em simulações Doom) para terapias de movimento revolucionárias."),
                        criarMedico("Dr. Anders Hejlsberg", "anders@med.com", senhaPadrao, "000014/SP", "11900000014", "Nefrologia", "Arquiteto de sistemas renais robustos. Conhecido por sua 'tipagem forte' na filtragem de toxinas, garantindo a integridade dos fluidos e estabilidade sistêmica contínua."),
                        criarMedico("Dra. Barbara Liskov", "barbara@med.com", senhaPadrao, "000015/SP", "11900000015", "Pneumologia", "Pioneira dos princípios sólidos (SOLID). Criou o rigoroso princípio da substituição saudável, essencial na estruturação de vias respiratórias e modulação clínica moderna.")
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
                System.out.println("✅ [DATA SEEDER] Lendas médicas inseridas com Biografias brilhantes! Agendas lotadas a partir de 12/05/2026!");
            }
        };
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
        medico.setBiografia(biografia); // NOVO: Inserção da Biografia!
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