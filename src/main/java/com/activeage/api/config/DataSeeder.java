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
            long qtdUsuarios = usuarioRepository.count();

            if (qtdUsuarios == 0) {
                System.out.println("🌱 [DATA SEEDER] Iniciando a semeadura de dados com Lendas da Programação...");

                String senhaPadrao = passwordEncoder.encode("senha123");
                String senhaAdmin = passwordEncoder.encode("admin1234");

                Usuario admin = new Usuario();
                admin.setNome("Administrador Active Age");
                admin.setEmail("admin@email.com");
                admin.setSenha(senhaAdmin);
                admin.setTipo(TipoUsuario.ADMIN);
                usuarioRepository.save(admin);
                System.out.println("✅ Administrador criado (admin@email.com)");

                Usuario pacienteDummy = new Usuario();
                pacienteDummy.setNome("Sr. Bug (Paciente Teste)");
                pacienteDummy.setEmail("paciente@teste.com");
                pacienteDummy.setSenha(senhaPadrao);
                pacienteDummy.setTipo(TipoUsuario.PACIENTE);
                pacienteDummy.setCpf("000.000.000-00");
                usuarioRepository.save(pacienteDummy);

                List<Usuario> lendasTech = Arrays.asList(
                        criarMedico("Dr. Linus Torvalds", "linus@med.com", senhaPadrao, "000001/SP", "11900000001", "Cirurgia Vascular",
                                "Engenheiro de software finlandês-americano, célebre mundialmente por ter criado o kernel do Linux e o sistema de controle de versão Git. Focado no código aberto e na colaboração estruturada."),

                        criarMedico("Dra. Ada Lovelace", "ada@med.com", senhaPadrao, "000002/SP", "11900000002", "Neurologia",
                                "Matemática e escritora inglesa do século XIX, reconhecida como a primeira programadora da história por ter escrito o primeiro algoritmo concebido para ser processado por uma máquina (a Máquina Analítica de Babbage)."),

                        criarMedico("Dr. Alan Turing", "alan@med.com", senhaPadrao, "000003/SP", "11900000003", "Psiquiatria",
                                "Matemático e lógico britânico. Considerado o pai da ciência da computação teórica e da inteligência artificial. Famoso também por seu papel crucial na quebra do código Enigma durante a Segunda Guerra Mundial."),

                        criarMedico("Dra. Grace Hopper", "grace@med.com", senhaPadrao, "000004/SP", "11900000004", "Infectologia",
                                "Almirante da Marinha dos EUA e brilhante analista de sistemas. Criadora da primeira linguagem de programação baseada em palavras em inglês, que serviu de base estrutural para a lendária linguagem COBOL."),

                        criarMedico("Dr. Dennis Ritchie", "dennis@med.com", senhaPadrao, "000005/SP", "11900000005", "Ortopedia",
                                "Cientista da computação estadunidense, mundialmente famoso por ser o criador da linguagem de programação C e co-criador do sistema operacional Unix, fundamentos que moldaram a computação moderna."),

                        criarMedico("Dr. Ken Thompson", "ken@med.com", senhaPadrao, "000006/SP", "11900000006", "Clínica Médica",
                                "Cientista pioneiro, parceiro de Dennis Ritchie na Bell Labs. Co-criador do Unix e autor principal da linguagem B (predecessora da C). Mais tarde, ajudou a conceber a linguagem Go na Google."),

                        criarMedico("Dra. Margaret Hamilton", "margaret@med.com", senhaPadrao, "000007/SP", "11900000007", "Cardiologia",
                                "Cientista da computação, engenheira de software e diretora da Divisão de Engenharia de Software do MIT. Desenvolveu o código de voo responsável pelo pouso da missão Apollo 11 na Lua."),

                        criarMedico("Dr. Bjarne Stroustrup", "bjarne@med.com", senhaPadrao, "000008/SP", "11900000008", "Gastroenterologia",
                                "Cientista da computação dinamarquês. Inventor e principal impulsionador da linguagem C++, uma das linguagens baseadas em orientação a objetos mais influentes e duradouras da indústria."),

                        criarMedico("Dr. James Gosling", "james@med.com", senhaPadrao, "000009/SP", "11900000009", "Endocrinologia",
                                "Cientista da computação canadense, amplamente reconhecido como o 'Pai do Java'. Consagrou o conceito de 'escreva uma vez, rode em qualquer lugar' através da Máquina Virtual Java (JVM)."),

                        criarMedico("Dr. Guido van Rossum", "guido@med.com", senhaPadrao, "000010/SP", "11900000010", "Dermatologia",
                                "Programador holandês criador do Python, uma linguagem projetada para ter uma sintaxe limpa, bonita e legível, hoje sendo o pilar absoluto da área de dados e inteligência artificial."),

                        criarMedico("Dr. Brendan Eich", "brendan@med.com", senhaPadrao, "000011/SP", "11900000011", "Oftalmologia",
                                "Programador norte-americano responsável pela criação do JavaScript em apenas 10 dias. Revolucionou a interatividade web e foi co-fundador do projeto Mozilla e do navegador Brave."),

                        criarMedico("Dr. Tim Berners-Lee", "tim@med.com", senhaPadrao, "000012/SP", "11900000012", "Medicina de Família e Comunidade",
                                "O inventor da World Wide Web (WWW). Físico britânico do CERN que idealizou o sistema de documentos de hipertexto interligados, unindo o mundo inteiro numa única teia de comunicação."),

                        criarMedico("Dr. John Carmack", "john@med.com", senhaPadrao, "000013/SP", "11900000013", "Fisiatria (Medicina de Reabilitação)",
                                "Gênio da programação gráfica e co-fundador da id Software. Criador de inovações absolutas em otimização matemática para motores 3D, fundando as bases para jogos como Doom e Quake."),

                        criarMedico("Dr. Anders Hejlsberg", "anders@med.com", senhaPadrao, "000014/SP", "11900000014", "Nefrologia",
                                "Eminente engenheiro de software dinamarquês responsável pela arquitetura do Turbo Pascal e Delphi. Mais tarde, revolucionou a Microsoft criando as linguagens C# e TypeScript."),

                        criarMedico("Dra. Barbara Liskov", "barbara@med.com", senhaPadrao, "000015/SP", "11900000015", "Pneumologia",
                                "Cientista ganhadora do Prêmio Turing e criadora do 'Princípio da Substituição de Liskov', pilar fundamental dos princípios S.O.L.I.D. na engenharia de software e orientação a objetos.")
                );

                usuarioRepository.saveAll(lendasTech);

                LocalDateTime dataBase = LocalDateTime.of(2026, 9, 1, 8, 0).truncatedTo(ChronoUnit.MINUTES);
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
                System.out.println("✅ [DATA SEEDER] Tudo pronto! Admin, Médicos(Lendas) e Agendas inseridos no banco com sucesso!");
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
        medico.setBiografia(biografia);
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