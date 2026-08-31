# 🧠 Active Age — Core API & Backend Central
 
<p align="center">
<img src="https://img.shields.io/badge/Deploy-Render-black?style=for-the-badge&logo=render" alt="Render Deploy" />
<img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 21" />
<img src="https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white%22 alt="Spring Boot 3" />
<img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white%22 alt="Spring Security" />
<img src="https://img.shields.io/badge/MongoDB-Atlas-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB Atlas" />
</p>
 
---
 
## 🌐 API em Produção
 
Acesse o Endpoint base da nossa API hospedada na nuvem:  
👉 **[https://active-age-backend.onrender.com](https://active-age-backend.onrender.com)**
 
---
 
## 💡 Sobre o Projeto
 
A **Core API do Active Age** é a espinha dorsal de todo o ecossistema de telemedicina. Construída sob a arquitetura RESTful, ela centraliza a lógica de negócios, o armazenamento de dados não-relacionais, e o roteamento de permissões.
 
Esta aplicação atua como o "cérebro" do sistema, processando agendas, emitindo tokens de segurança e comunicando-se de forma assíncrona com microserviços externos (como o de pagamentos) através de Webhooks.
 
---
 
## ✨ Principais Funcionalidades
 
### 🔐 Gestão de Perfis & Autenticação
- **Autenticação Stateless:** Controle de sessão totalmente baseado em Tokens JWT (JSON Web Tokens).
- **Autorização por Papéis (Roles):** Sistema estruturado em 3 níveis de acesso: `PACIENTE`, `MEDICO` e `ADMIN`.
- **Validação de Cadastro:** Bloqueio inteligente de funcionalidades para médicos até que o `ADMIN` aprove oficialmente o CRM na plataforma.
 
### 📅 Motor de Agendamentos
- **Algoritmo Anti-Conflitos:** Lógica restritiva que impede a marcação de consultas que sobreponham o intervalo mínimo de segurança (40 minutos entre atendimentos).
- **Gestão de Horários Livres:** Disponibilização e cancelamento em tempo real de "slots" de agenda na coleção NoSQL de agendamentos.
 
### 📡 Eventos & Cross-Service (Microserviços)
- **Webhook Receiver:** Endpoint exclusivo (`/api/usuarios/medicos/{id}/assinatura/ativar`) configurado para ouvir requisições do Microserviço de Pagamentos e ativar imediatamente a assinatura do médico no banco de dados, ignorando a esteira normal de autenticação por segurança.
 
### 🌱 Data Seeding Dinâmico
- **Semeadura Inteligente:** Classe `DataSeeder` implementada para popular o banco de dados inicial (caso esteja vazio) com perfis fictícios de médicos (usando Lendas da Computação) e geração automática de agendas. Ideal para testes rápidos e bancas de apresentação.
 
---
 
## 🛠️ Tecnologias Utilizadas
 
- **Linguagem:** [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Framework Principal:** [Spring Boot 3](https://spring.io/projects/spring-boot)
- **Segurança:** [Spring Security](https://spring.io/projects/spring-security) com JWT
- **Banco de Dados:** [MongoDB Atlas](https://www.mongodb.com/atlas) (NoSQL) manipulado via Spring Data MongoDB
- **Gerenciador de Dependências:** [Maven](https://maven.apache.org/)
- **Utilitários:** [Lombok](https://projectlombok.org/) (Redução de Boilerplate code)
 
---
 
## 📁 Estrutura de Pastas (Arquitetura MVC)
 
```bash
src/main/java/com/activeage/api/
├── config/             # Configurações de CORS, WebSecurity e DataSeeder
├── controller/         # Endpoints RESTful e roteamento HTTP (API)
├── dto/                # Data Transfer Objects (Isolamento de payloads e sanitização)
├── enums/              # Constantes fortemente tipadas (Status, Ciclos, etc.)
├── model/              # Entidades mapeadas para Documentos MongoDB (@Document)
├── repository/         # Interfaces de persistência (Spring Data MongoRepository)
├── security/           # Filtros de cadeia do JWT e provedores de autenticação
└── service/            # Camada central com todas as regras de negócios
```
## ⚙️ Como Executar Localmente
 
### Pré-requisitos
- JDK 21 instalado
- Maven instalado
- Uma instância do MongoDB (Local ou Atlas)
 
### Passos
1. Clone este repositório.
2. Na raiz do projeto, configure a variável de ambiente principal definindo a string de conexão:
   `MONGO_URI=mongodb+srv://<usuario>:<senha>@cluster0.mongodb.net/active_age`
3. Execute o comando de inicialização do Maven:
   `mvn spring-boot:run`
4. A API subirá no servidor embutido do Tomcat na porta `8080`.
 
---
 
## 🔒 Segurança e Privacidade
 
- **Criptografia:** Todas as senhas são criptografadas com `BCryptPasswordEncoder` antes da persistência.
- **Isolamento de Dados:** Pacientes não possuem acesso aos endpoints administrativos; rotas sensíveis exigem verificação do `Bearer Token`.
 
---
 
## 📄 Licença
 
Este projeto foi desenvolvido como um sistema acadêmico focado em inovação para saúde digital e arquitetura de microserviços.
