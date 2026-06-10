# 🧩 Microserviços com Spring Boot — Do Monolito ao Universo Distribuído

> 🌿 Branch: `feature/microservicos`
> 🎯 Entidade de exemplo: **Matrícula**
> 🔌 Porta: **8081**

---

## 🏠 Era uma vez... um Monolito

Imagine uma casa onde **todos moram no mesmo cômodo**: cozinha, quarto, escritório e garagem, tudo junto. Funciona, mas quando a garagem pega fogo, a cozinha também sofre as consequências.

É assim que funciona uma aplicação **monolítica**: um único projeto, um único banco, uma única porta. Se uma parte trava, tudo pode travar junto.

```
[ crudmongo - porta 8080 ]
┌─────────────────────────┐
│  👤 Pessoa              │
│  📚 Curso               │  ← Tudo numa casa só
│  📝 Disciplina          │
│  👨‍🏫 Professor           │
│  📋 Matrícula           │
│  🗄️ Banco: crudmongo    │
└─────────────────────────┘
```

---

## 🏘️ A solução: um bairro de microserviços!

No modelo de **microserviços**, cada entidade vira uma casinha independente. Se uma pega fogo, as outras continuam de pé.

```
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  👤 pessoa   │   │  📚 curso    │   │  📝 disciplina│
│   svc:8082   │   │   svc:8083   │   │    svc:8084   │
│  🗄️ pessoadb │   │  🗄️ cursodb  │   │  🗄️ discipldb │
└──────────────┘   └──────────────┘   └──────────────┘

┌──────────────┐   ┌──────────────┐
│  👨‍🏫 professor│   │  📋 matricula │  ← este é o nosso exemplo!
│   svc:8085   │   │   svc:8081   │
│  🗄️ profdb   │   │  🗄️ matriculadb│
└──────────────┘   └──────────────┘
```

Cada casinha tem: **sua própria porta**, **seu próprio banco** e **suas próprias responsabilidades**. Elas conversam entre si via **chamadas HTTP REST** — como vizinhos que trocam mensagens pelo interfone.

---

## ⚖️ Monolito vs Microserviço — A comparação honesta

| O que muda?          | Monolito (`crudmongo`)          | Microserviço (`matricula-service`) |
|----------------------|---------------------------------|------------------------------------|
| 🔌 Porta             | 8080                            | 8081                               |
| 🗄️ Banco             | `crudmongo` (compartilhado)     | `matriculadb` (exclusivo)          |
| 📦 Deploy            | Um único JAR gordo              | JARs independentes e leves         |
| 🔗 Relacionamentos   | `@ManyToOne` entre entidades    | Apenas IDs (`pessoaId`, `cursoId`) |
| 💬 Comunicação       | Chamada direta de método Java   | HTTP REST entre serviços           |
| 📈 Escala            | Escala tudo ou nada             | Escala só o que está sofrendo      |

---

## � Antes e Depois — O que mudou de verdade no código

Chega de teoria! Veja exatamente o que existia no monolito (`crudmongo`) e como ficou no microserviço (`matricula-service`).

---

### 📁 Estrutura do projeto

**Antes — tudo no mesmo projeto:**
```
backend/src/main/java/com/exemplo/crudmongo/
├── controller/
│   ├── MatriculaController.java   ← junto com Pessoa, Curso, Professor...
│   ├── PessoaController.java
│   └── CursoController.java
├── Model/
│   ├── Matricula.java
│   ├── Pessoa.java                ← Matrícula conhecia Pessoa diretamente
│   └── Curso.java
└── repository/
    └── MatriculaRepository.java
```

**Depois — projeto isolado, responsabilidade única:**
```
microservicos/matricula-service/src/main/java/com/exemplo/matriculaservice/
├── controller/
│   └── MatriculaController.java   ← só matrícula aqui
├── model/
│   └── Matricula.java             ← não conhece Pessoa nem Curso
└── repository/
    └── MatriculaRepository.java
```

---

### 🔗 Relacionamento entre entidades

**Antes — acoplamento direto com `@ManyToOne`:**
```java
// Matricula.java no monolito
@ManyToOne
@JoinColumn(name = "pessoa_id")
private Pessoa pessoa;       // ← importava a classe Pessoa do mesmo projeto

@ManyToOne
@JoinColumn(name = "curso_id")
private Curso curso;         // ← importava a classe Curso do mesmo projeto
```

**Depois — apenas IDs, sem acoplamento:**
```java
// Matricula.java no microserviço
private Long pessoaId;       // ← só guarda o ID; Pessoa mora em outro serviço
private Long cursoId;        // ← só guarda o ID; Curso mora em outro serviço
```

> 🧠 **Por que isso importa?** No monolito, se a classe `Pessoa` mudasse, o código de `Matricula` poderia quebrar. No microserviço, `Matricula` não sabe nem que `Pessoa` existe — ela só anota o número.

---

### ⚙️ Configuração do banco

**Antes — banco compartilhado entre todas as entidades:**
```properties
# application.properties do crudmongo (backend)
spring.data.mongodb.uri=mongodb://localhost:27017/crudmongo
# Pessoa, Curso, Matricula, Professor... todos no mesmo banco
```

**Depois — banco exclusivo, isolado:**
```properties
# application.properties do matricula-service
server.port=8081
spring.datasource.url=jdbc:h2:mem:matriculadb   # banco só de matrícula
spring.application.name=matricula-service
```

---

### 🔐 Segurança (Spring Security)

**Antes — toda requisição passava pelo filtro JWT:**
```java
// SecurityConfig.java no monolito
// Cada endpoint exigia token e validação de role
.requestMatchers("/api/matriculas/**").hasAnyRole("PROFESSOR", "ALUNO")
```

**Depois — serviço aberto para facilitar o aprendizado:**
```java
// No microserviço didático, a segurança foi removida intencionalmente
// para que os alunos foquem na arquitetura, não na autenticação.
// Em produção real, cada microserviço teria seu próprio mecanismo de auth
// ou usaria um API Gateway central.
```

---

### 📡 Como acessar a entidade Matrícula

**Antes — uma URL, tudo em um servidor:**
```
GET http://localhost:8080/api/matriculas   ← monolito na porta 8080
```

**Depois — serviço dedicado com porta própria:**
```
GET http://localhost:8081/api/matriculas   ← microserviço na porta 8081
```

---

### 🏁 Resumo visual do antes e depois

```
ANTES (Monolito)
─────────────────────────────────────────────────────
  [ browser ]
       │
       ▼
  [ crudmongo :8080 ]
  ┌────────────────────────────────────────────┐
  │  /api/pessoas   /api/cursos  /api/matriculas│
  │  /api/turmas    /api/professores  ...       │
  │                                            │
  │  🗄️ Um único banco para tudo               │
  └────────────────────────────────────────────┘


DEPOIS (Microserviços)
─────────────────────────────────────────────────────
  [ browser ]
       │
  ┌────┴────┬──────────────┬──────────────┐
  ▼         ▼              ▼              ▼
[:8082]   [:8083]        [:8084]        [:8081]
pessoa    curso          disciplina     matricula
🗄️ propdb 🗄️ cursodb    🗄️ discipldb   🗄️ matriculadb
```

---

## �🗂️ Estrutura de arquivos — O mapa da casinha

```
microservicos/
└── matricula-service/
    ├── pom.xml                          ← lista de ingredientes
    └── src/main/
        ├── java/com/exemplo/matriculaservice/
        │   ├── MatriculaServiceApplication.java  ← a porta de entrada
        │   ├── config/
        │   │   └── MatriculaDataLoader.java      ← dados de teste iniciais
        │   ├── controller/
        │   │   └── MatriculaController.java      ← quem atende as requisições
        │   ├── model/
        │   │   └── Matricula.java                ← o molde do objeto
        │   ├── repository/
        │   │   └── MatriculaRepository.java      ← quem fala com o banco
        │   └── service/
        │       └── MatriculaService.java         ← as regras do negócio
        └── resources/
            └── application.properties           ← as configurações
```

---

## 🔍 Destrinchando cada arquivo

---

### 📦 `pom.xml` — A lista de ingredientes

Diferente do monolito que tem dezenas de dependências, o microserviço usa **apenas o necessário**. Sem Spring Security, sem JWT — só o básico para gerenciar matrículas:

```xml
spring-boot-starter-web       <!-- para criar a API REST -->
spring-boot-starter-data-jpa  <!-- para salvar no banco    -->
h2                            <!-- o banco em memória      -->
```

> 💡 **Dica:** pense no `pom.xml` como a lista de compras do supermercado. Você só compra o que vai usar — não precisa de farinha se vai fazer uma salada.

---

### ⚙️ `application.properties` — As configurações da casinha

```properties
server.port=8081                              # nossa porta exclusiva
spring.application.name=matricula-service    # nosso nome no bairro
spring.datasource.url=jdbc:h2:mem:matriculadb # nosso banco exclusivo
```

> 🏠 **Regra de ouro do bairro:** **nenhuma casinha divide o banco com outra**. Isso se chama **Database per Service** — cada serviço é dono dos seus próprios dados.

---

### 🧱 `model/Matricula.java` — O molde do objeto

Aqui mora a diferença mais importante dos microserviços. Preste atenção:

```java
// ❌ NO MONOLITO funcionava assim:
@ManyToOne
private Pessoa pessoa;   // importava a classe Pessoa direto!

// ✅ NO MICROSERVIÇO fazemos assim:
private Long pessoaId;   // guardamos apenas o ID
private Long cursoId;    // o serviço de Pessoa fica na casinha dele
```

**Por que não usar `@ManyToOne`?**
Porque `Pessoa` pertence ao serviço de pessoas. Se importarmos a classe `Pessoa` aqui, as duas casinhas ficam **grudadas** — e aí voltamos ao problema do monolito. Queremos vizinhos independentes, não siameses!

---

### 🔎 `repository/MatriculaRepository.java` — O bibliotecário do banco

Além dos métodos automáticos do `JpaRepository`, ensinamos o Spring a buscar dados por campos específicos — apenas escrevendo o nome certo do método:

```java
findByPessoaId(Long pessoaId)  // → SELECT * FROM matricula WHERE pessoa_id = ?
findByCursoId(Long cursoId)    // → SELECT * FROM matricula WHERE curso_id = ?
findByAtivo(boolean ativo)     // → SELECT * FROM matricula WHERE ativo = ?
```

> 🪄 **Mágica do Spring Data:** o framework lê o nome do método e **gera o SQL sozinho**. Sem escrever uma linha de SQL!

---

### 🧠 `service/MatriculaService.java` — O cérebro da operação

Toda a inteligência do serviço fica aqui. O Controller só recebe pedidos, o Service decide o que fazer com eles.

Destaque especial para o **soft delete** — uma técnica onde não apagamos o registro, apenas o "desligamos":

```java
// Em vez de deletar do banco, apenas marcamos como inativo
public void desativar(Long id) {
    Matricula m = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Matricula nao encontrada: " + id));
    m.setAtivo(false);   // o registro continua no banco!
    repository.save(m);
}
```

> 🗃️ **Por que soft delete?** Imagine cancelar uma matrícula e perder todo o histórico do aluno. Com soft delete, o registro fica guardado para fins de auditoria — apenas invisível nas listagens normais.

---

### 🚪 `controller/MatriculaController.java` — A recepção do serviço

O Controller é a porta de entrada da casinha. Ele recebe as requisições HTTP e passa para o Service resolver:

| Método   | URL                              | O que faz                           |
|----------|----------------------------------|-------------------------------------|
| `GET`    | `/api/matriculas`                | 📋 Lista todas as matrículas        |
| `GET`    | `/api/matriculas/{id}`           | 🔍 Busca uma pelo ID                |
| `GET`    | `/api/matriculas/pessoa/{id}`    | 👤 Todas as matrículas de uma pessoa|
| `GET`    | `/api/matriculas/curso/{id}`     | 📚 Todas as matrículas de um curso  |
| `POST`   | `/api/matriculas`                | ➕ Cria uma nova (retorna 201)      |
| `PUT`    | `/api/matriculas/{id}`           | ✏️ Atualiza todos os dados          |
| `PATCH`  | `/api/matriculas/{id}/desativar` | 🔴 Desativa (soft delete)           |
| `DELETE` | `/api/matriculas/{id}`           | 🗑️ Remove permanentemente           |

---

### 🌱 `config/MatriculaDataLoader.java` — O jardineiro dos dados

Toda vez que o serviço sobe, este arquivo planta 4 matrículas de exemplo no banco H2. Assim você já tem dados para testar sem precisar cadastrar nada manualmente:

```java
repository.save(new Matricula(1L, 1L, "2024-01-10", true));
repository.save(new Matricula(1L, 2L, "2024-01-15", true));
// ... e mais 2
```

---

## 🚀 Subindo o serviço

```bash
# Entre na pasta do microserviço
cd microservicos/matricula-service

# Suba o servidor
mvn spring-boot:run
```

Quando aparecer `Started MatriculaServiceApplication in X.XX seconds`, o serviço está pronto! Acesse:

- 🌐 **API:** `http://localhost:8081/api/matriculas`
- 🗄️ **H2 Console:** `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:matriculadb`
  - Usuário: `sa` | Senha: `sa`

---

## 🧪 Testando na prática

### Ver todas as matrículas
```http
GET http://localhost:8081/api/matriculas
```

### Ver matrículas de uma pessoa
```http
GET http://localhost:8081/api/matriculas/pessoa/1
```

### Criar uma nova matrícula
```http
POST http://localhost:8081/api/matriculas
Content-Type: application/json

{
  "pessoaId": 1,
  "cursoId": 2,
  "dataMatricula": "2024-05-11",
  "ativo": true
}
```

### Cancelar uma matrícula (sem apagar do banco)
```http
PATCH http://localhost:8081/api/matriculas/1/desativar
```

---

## 🎓 Atividade (Próximas duas aulas) Como criar o seu próprio microserviço

Agora é com você! Escolha uma entidade, faça a implementação seguindo o roteiro:

### Passo 1 — Crie a estrutura
Crie a pasta `microservicos/<suaentidade>-service/` com a mesma estrutura de pastas do exemplo.

### Passo 2 — Configure o `pom.xml`
Copie o `pom.xml` do `matricula-service` e troque:
- `<artifactId>matricula-service</artifactId>` → `<artifactId>suaentidade-service</artifactId>`
- `<name>` e `<description>` para o seu serviço

### Passo 3 — Configure o `application.properties`
```properties
server.port=8082                           # porta nova (8082, 8083, ...)
spring.application.name=suaentidade-service
spring.datasource.url=jdbc:h2:mem:suaentidadedb
```

### Passo 4 — Crie as 5 classes
Seguindo sempre este padrão:

| Classe        | Responsabilidade                                  | Lembrete especial                   |
|---------------|---------------------------------------------------|-------------------------------------|
| `model/`      | Define os campos da entidade com `@Entity`        | Use IDs para referências externas   |
| `repository/` | Estende `JpaRepository`, queries por nome         | Sem SQL manual!                     |
| `service/`    | Lógica de negócio, injeção via construtor         | Nunca coloque lógica no Controller  |
| `controller/` | Endpoints REST com `@RestController`              | Delegue tudo ao Service             |
| `config/`     | `DataLoader` com dados iniciais para testes       | Implemente `CommandLineRunner`      |

### Passo 5 — Crie a classe principal
```java
@SpringBootApplication
public class SuaEntidadeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuaEntidadeServiceApplication.class, args);
    }
}
```

---

> ⚠️ **Regra de ouro:** se o seu serviço precisa de informações de outro serviço (ex: buscar o nome da Pessoa pelo ID), faça uma **chamada HTTP REST** para o serviço de Pessoa. **Nunca importe a classe Java de outro microserviço** — isso recria o acoplamento que estamos tentando evitar.

---

## 🚀 Desafios de Evolução do Projeto

Abaixo estão os desafios para você continuar evoluindo o projeto, do mais simples ao mais avançado.

---

### 🟢 Nível 1 — Replicar o padrão (obrigatório)

> **Objetivo:** consolidar o que foi aprendido criando novos microserviços seguindo exatamente o mesmo padrão do `matricula-service`.

Crie um microserviço para **cada entidade abaixo**, seguindo os passos do guia anterior:

| Entidade      | Sugestão de porta | Nome do banco     | Branch sugerida              |
|---------------|-------------------|-------------------|------------------------------|
| 👤 Pessoa     | 8082              | `pessoadb`        | `feature/pessoa-service`     |
| 📚 Curso      | 8083              | `cursodb`         | `feature/curso-service`      |
| 📝 Disciplina | 8084              | `disciplinadb`    | `feature/disciplina-service` |
| 👨‍🏫 Professor  | 8085              | `professordb`     | `feature/professor-service`  |
| 🏫 Turma      | 8086              | `turmadb`         | `feature/turma-service`      |

**Entregável:** cada serviço deve subir com `mvn spring-boot:run` e responder em sua respectiva porta.

---

### 🟡 Nível 2 — Comunicação entre serviços

> **Objetivo:** fazer dois microserviços conversarem via HTTP, sem compartilhar código Java.

**Desafio:** ao buscar uma matrícula pelo ID, retorne também o **nome da pessoa** e o **nome do curso** — buscando essas informações nos respectivos microserviços via `RestTemplate` ou `WebClient`.

Exemplo do retorno esperado:
```json
{
  "id": 1,
  "pessoaId": 1,
  "nomePessoa": "Ana Silva",       ← buscado do pessoa-service (8082)
  "cursoId": 2,
  "nomeCurso": "Engenharia",       ← buscado do curso-service (8083)
  "dataMatricula": "2024-01-10",
  "ativo": true
}
```

**Dica:** crie uma classe `MatriculaDetalhadaDTO` para montar essa resposta no `MatriculaService`, sem alterar a entidade `Matricula`.

---

### 🟠 Nível 3 — Tratamento de erros e resiliência

> **Objetivo:** deixar o sistema robusto quando um serviço vizinho estiver fora do ar.

**Desafios:**

- **3.1 — Handler global de erros:** crie uma classe `@ControllerAdvice` que capture `RuntimeException` e devolva respostas JSON padronizadas com `status`, `mensagem` e `timestamp`, em vez do Whitelabel Error Page.

- **3.2 — Fallback:** na comunicação entre serviços (Nível 2), se o `pessoa-service` estiver fora, o `matricula-service` **não deve cair junto**. Retorne `"nomePessoa": "indisponível"` como fallback em vez de propagar o erro.

---

### 🔴 Nível 4 — Dockerizando o bairro

> **Objetivo:** empacotar cada microserviço em um container Docker e orquestrá-los juntos.

**Desafio:** crie um `Dockerfile` para o `matricula-service` e adicione-o ao `docker-compose.yml` da raiz do projeto, de forma que todos os serviços subam com um único comando:

```bash
docker-compose up --build
```

**Referência:** veja o `Dockerfile` e o `docker-compose.yml` já existentes na raiz do projeto como modelo.

---

### ⚫ Nível 5 — API Gateway 

> **Objetivo:** criar um ponto de entrada único para todos os microserviços, como uma portaria do condomínio.

**Opção A — Spring Cloud Gateway** *(recomendada)*

Cria um novo projeto `gateway-service` com Spring Cloud Gateway e configura as rotas no `application.properties`:

```properties
server.port=8080
spring.cloud.gateway.routes[0].id=matricula
spring.cloud.gateway.routes[0].uri=http://localhost:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/matriculas/**

spring.cloud.gateway.routes[1].id=pessoa
spring.cloud.gateway.routes[1].uri=http://localhost:8082
spring.cloud.gateway.routes[1].predicates[0]=Path=/pessoas/**
```

---

**Opção B — Gateway manual com Spring Boot** *(sem dependências novas — funciona offline)*

Crie um projeto `gateway-service` usando apenas `spring-boot-starter-web` (já presente em todos os microserviços) e implemente o roteamento manualmente com `RestTemplate`:

```java
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Redireciona /gateway/matriculas/** → http://localhost:8081/api/matriculas
    @GetMapping("/matriculas")
    public ResponseEntity<String> matriculas() {
        return restTemplate.getForEntity(
            "http://localhost:8081/api/matriculas", String.class);
    }

    // Redireciona /gateway/pessoas/** → http://localhost:8082/api/pessoas
    @GetMapping("/pessoas")
    public ResponseEntity<String> pessoas() {
        return restTemplate.getForEntity(
            "http://localhost:8082/api/pessoas", String.class);
    }
}
```

**Como fica a arquitetura:**
```
[ cliente ]
    │
    ▼
[ gateway-service :8080 ]  ← portaria única (Opção B)
    │              │
    ▼              ▼
 :8081          :8082      ...
matricula       pessoa
```

> 💡 A Opção B não é um gateway de produção, mas demonstra o **conceito** de ponto de entrada único — que é o objetivo do exercício. A Opção A (Spring Cloud) seria o caminho em um projeto real com internet disponível.

---

> 💬 **Uma última palavra:** microserviços não são a solução para tudo. Eles trazem complexidade operacional — mais processos, mais deploys, mais pontos de falha. O monolito do `crudmongo` é perfeitamente válido para projetos pequenos. O objetivo aqui foi entender os **conceitos e o padrão** — para que, quando o projeto crescer de verdade, você saiba como e quando separar as responsabilidades. 🏗️
