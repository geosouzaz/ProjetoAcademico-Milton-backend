# Documentação — Sistema Acadêmico Full Stack

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Arquitetura](#2-arquitetura)
3. [Stack Tecnológica](#3-stack-tecnológica)
4. [Estrutura de Pastas](#4-estrutura-de-pastas)
5. [Camada 1 — Backend Monolito](#5-camada-1--backend-monolito)
6. [Camada 2 — Microserviços](#6-camada-2--microserviços)
7. [Gateway e Segurança](#7-gateway-e-segurança)
8. [Frontend Angular](#8-frontend-angular)
9. [Docker e Infraestrutura](#9-docker-e-infraestrutura)
10. [Como Executar](#10-como-executar)
11. [Endpoints — Referência Completa](#11-endpoints--referência-completa)
12. [Exemplos de Requisições](#12-exemplos-de-requisições)

---

## 1. Visão Geral

Sistema de cadastro acadêmico desenvolvido com Java, Spring Boot e Angular. O projeto tem **duas camadas de backend independentes** que coexistem:

| Camada | Pasta | Propósito |
|---|---|---|
| Backend monolito | `backend/` | Aplicação Spring Boot única com todas as entidades e autenticação JWT |
| Microserviços | `microservicos/` | Versão distribuída com um serviço por domínio e gateway centralizado |
| Frontend | `frontend/` | SPA Angular que consome os microserviços via gateway |

O domínio do sistema é escolar: gerencia **Pessoas**, **Cursos**, **Disciplinas**, **Professores**, **Turmas** e **Matrículas**, com controle de acesso baseado em papéis (PROFESSOR e ALUNO).

---

## 2. Arquitetura

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTE                              │
│              (Browser / Postman / curl)                     │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP
                            ▼
┌───────────────────────────────────────────────────────────┐
│                   GATEWAY (porta 8080)                    │
│              Spring Cloud Gateway + WebFlux               │
│         Valida JWT · Aplica RBAC · Roteia requests        │
└──┬──────┬──────┬──────┬──────┬──────┬──────┬─────────────┘
   │      │      │      │      │      │      │
   ▼      ▼      ▼      ▼      ▼      ▼      ▼
 AUTH   MATR  PESSOA  CURSO  DISC  PROF  TURMA
 8090   8081   8082   8083   8084  8085  8086
  │      │      │      │
  │      └──────┴──────┘ (inter-service via RestTemplate)
  │
  └── Backend monolito (auth) — porta interna 8080
```

### Fluxo de uma requisição autenticada

```
1. Cliente faz POST /api/auth/login  →  gateway roteia para app:8080
2. Backend valida credenciais         →  retorna JWT assinado (HS256)
3. Cliente usa o JWT no header        →  Authorization: Bearer <token>
4. Gateway valida a assinatura JWT    →  extrai role do claim "role"
5. Gateway aplica RBAC                →  ALUNO só GET, PROFESSOR tudo
6. Gateway injeta headers             →  X-Auth-User, X-Auth-Role
7. Microserviço recebe a requisição   →  processa e responde
```

### Comunicação inter-serviços (Matricula)

O `matricula-service` é o único serviço que chama outros. Quando um cliente pede `GET /api/matriculas/{id}`, o serviço:

```
matricula-service
  ├── busca a Matricula no próprio H2
  ├── chama pessoa-service:8082 /api/pessoas/{pessoaId}  → obtém nomePessoa
  └── chama curso-service:8083  /api/cursos/{cursoId}    → obtém nomeCurso
  └── retorna MatriculaDetalhadaDTO com os três campos resolvidos
```

Se pessoa-service ou curso-service estiver fora do ar, o campo retorna `"indisponivel"` em vez de derrubar o serviço.

---

## 3. Stack Tecnológica

### Backend (monolito e microserviços)

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem base |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Security | (incluso no Boot) | Autenticação e RBAC |
| Spring Cloud Gateway | 2023.x | Gateway reativo (WebFlux) |
| Spring Data JPA | (incluso no Boot) | Persistência ORM |
| H2 Database | (incluso no Boot) | Banco em memória (dev) |
| JJWT | 0.12.5 | Geração e validação de tokens JWT |
| Lombok | 1.18.30 | Redução de boilerplate (monolito) |
| JavaFaker | 1.0.2 | Geração de dados seed (monolito) |
| Springdoc OpenAPI | 2.5.0 | Documentação Swagger (monolito) |
| Maven | 3.9.6 | Build e gestão de dependências |

### Frontend

| Tecnologia | Versão | Uso |
|---|---|---|
| Angular | 15 | Framework SPA |
| TypeScript | ~4.8 | Linguagem |
| nginx | latest | Servidor web + proxy reverso (Docker) |

### Infraestrutura

| Tecnologia | Uso |
|---|---|
| Docker | Containerização de todos os serviços |
| Docker Compose | Orquestração local dos 8 containers |

---

## 4. Estrutura de Pastas

```
crudfullstackSpring/
│
├── docker-compose.yml          # Orquestra os 8 containers
├── Dockerfile                  # Dockerfile raiz (não usado diretamente)
├── DOCUMENTACAO.md             # Este arquivo
│
├── backend/                    # Monolito Spring Boot (porta 8090 no Docker)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/exemplo/crudmongo/
│       ├── CrudMongoApplication.java
│       ├── Model/              # Entidades JPA
│       ├── repository/         # Interfaces JpaRepository
│       ├── service/            # Lógica de negócio
│       ├── controller/         # Endpoints REST
│       └── config/             # Security, JWT, DataLoaders
│
├── microservicos/              # Arquitetura distribuída
│   ├── gateway-service/        # Roteamento + segurança (porta 8080)
│   ├── pessoa-service/         # Domínio Pessoa (porta 8082)
│   ├── curso-service/          # Domínio Curso (porta 8083)
│   ├── disciplina-service/     # Domínio Disciplina (porta 8084)
│   ├── professor-service/      # Domínio Professor (porta 8085)
│   ├── turma-service/          # Domínio Turma (porta 8086)
│   └── matricula-service/      # Domínio Matrícula + inter-service (porta 8081)
│
├── frontend/                   # Angular 15
│   ├── Dockerfile
│   ├── docker-compose.yml      # Compose separado do frontend
│   ├── nginx.conf              # Proxy /api/ → gateway-service:8080
│   └── src/app/
│       ├── pessoas/            # Componente de listagem de pessoas
│       └── pessoas.service.ts  # Serviço HTTP para pessoas
│
└── diagramas/                  # Diagramas do projeto (Mermaid)
    ├── diagramas-entidades.md
    ├── diagramas-pessoa.md
    ├── diagramas-projeto.md
    └── diagrama-boot-sequencia.md
```

---

## 5. Camada 1 — Backend Monolito

O monolito é a versão original do projeto, uma aplicação Spring Boot única que concentra todas as entidades, a autenticação JWT e os endpoints REST. No Docker ele roda na porta **8090** (externa) → 8080 (interna), e é consumido exclusivamente pelo gateway para processar o login.

### 5.1 Entidades e Modelos

Todas as entidades usam JPA com banco H2 em memória.

#### Pessoa
```
id        Long     (PK auto)
nome      String
email     String
ativo     boolean
```

#### Curso
```
id            Long     (PK auto)
nome          String
cargaHoraria  int      (horas totais)
ativo         boolean
```

#### Professor
```
id            Long     (PK auto)
nome          String
especialidade String
ativo         boolean
```

#### Disciplina
```
id      Long     (PK auto)
nome    String
codigo  String   (ex: "MAT01")
ativo   boolean
```

#### Turma
```
id        Long     (PK auto)
nome      String
semestre  String   (ex: "2026.1")
ativo     boolean
```

#### Matricula
```
id      Long     (PK auto)
nome    String
ativo   boolean
```

#### Usuario (entidade interna de autenticação)
```
id        Long     (PK auto)
username  String
password  String   (BCrypt hash)
role      String   ("PROFESSOR" | "ALUNO")
```

### 5.2 Autenticação JWT

**Arquivo:** `config/JwtUtil.java`

- Algoritmo: **HS256**
- Chave secreta: `minha-chave-secreta-super-segura-32bytes!!`
- Expiração: **1 hora**
- Claims do token: `sub` (username), `role` (papel do usuário), `iat`, `exp`

**Arquivo:** `config/JwtAuthFilter.java`

Filtro que intercepta cada requisição, lê o header `Authorization: Bearer <token>`, valida a assinatura e seta o contexto de segurança do Spring.

**Arquivo:** `config/SecurityConfig.java`

```
/api/auth/login   → público (permitAll)
/h2-console/**    → público (para debug)
/api/auth/register→ apenas PROFESSOR
GET /**           → ALUNO ou PROFESSOR
POST /**          → apenas PROFESSOR
PUT /**           → apenas PROFESSOR
DELETE /**        → apenas PROFESSOR
```

### 5.3 Endpoints do Monolito (porta 8090)

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Autentica e retorna JWT |
| POST | `/api/auth/register` | PROFESSOR | Cadastra novo usuário |
| GET | `/api/pessoas` | ALUNO/PROFESSOR | Lista todas as pessoas |
| POST | `/api/pessoas` | PROFESSOR | Cria pessoa |
| PUT | `/api/pessoas/{id}` | PROFESSOR | Atualiza pessoa |
| DELETE | `/api/pessoas/{id}` | PROFESSOR | Remove pessoa (204) |
| GET | `/api/cursos` | ALUNO/PROFESSOR | Lista todos os cursos |
| POST | `/api/cursos` | PROFESSOR | Cria curso |
| PUT | `/api/cursos/{id}` | PROFESSOR | Atualiza curso |
| DELETE | `/api/cursos/{id}` | PROFESSOR | Remove curso (204) |
| GET | `/api/professores` | ALUNO/PROFESSOR | Lista professores |
| GET | `/api/professores/{id}` | ALUNO/PROFESSOR | Busca professor por ID |
| POST | `/api/professores` | PROFESSOR | Cria professor |
| PUT | `/api/professores/{id}` | PROFESSOR | Atualiza professor |
| DELETE | `/api/professores/{id}` | PROFESSOR | Remove professor (204) |
| GET | `/api/disciplinas` | ALUNO/PROFESSOR | Lista disciplinas |
| GET | `/api/disciplinas/{id}` | ALUNO/PROFESSOR | Busca disciplina por ID |
| POST | `/api/disciplinas` | PROFESSOR | Cria disciplina |
| PUT | `/api/disciplinas/{id}` | PROFESSOR | Atualiza disciplina |
| DELETE | `/api/disciplinas/{id}` | PROFESSOR | Remove disciplina (204) |
| GET | `/api/turmas` | ALUNO/PROFESSOR | Lista turmas |
| GET | `/api/turmas/{id}` | ALUNO/PROFESSOR | Busca turma por ID |
| POST | `/api/turmas` | PROFESSOR | Cria turma |
| PUT | `/api/turmas/{id}` | PROFESSOR | Atualiza turma |
| DELETE | `/api/turmas/{id}` | PROFESSOR | Remove turma (204) |
| GET | `/api/matriculas` | ALUNO/PROFESSOR | Lista matrículas |
| GET | `/api/matriculas/{id}` | ALUNO/PROFESSOR | Busca matrícula por ID |
| POST | `/api/matriculas` | PROFESSOR | Cria matrícula |
| PUT | `/api/matriculas/{id}` | PROFESSOR | Atualiza matrícula |
| DELETE | `/api/matriculas/{id}` | PROFESSOR | Remove matrícula (204) |

### 5.4 DataLoaders (dados seed)

Na inicialização o monolito popula o H2 automaticamente se estiver vazio:

| DataLoader | Registros criados |
|---|---|
| `PessoaDataLoader` | 200 pessoas com dados fake (JavaFaker pt-BR) |
| `CursoDataLoader` | 200 cursos com dados fake |
| `ProfessorDataLoader` | 2 professores fixos |
| `DisciplinaDataLoader` | 2 disciplinas fixas |
| `TurmaDataLoader` | 2 turmas fixas |
| `UsuarioDataLoader` | 2 usuários: `professor`/`prof123` e `aluno`/`aluno123` |

### 5.5 Swagger UI

Com o monolito rodando, a documentação interativa está disponível em:

```
http://localhost:8090/swagger-ui/index.html
```

---

## 6. Camada 2 — Microserviços

Cada serviço é uma aplicação Spring Boot independente com seu próprio banco H2 em memória. Eles se comunicam **somente via HTTP** — não há banco de dados compartilhado nem chamadas diretas de código.

### 6.1 pessoa-service (porta 8082)

**Modelo:**
```json
{ "id": 1, "nome": "Ana Silva", "email": "ana@email.com", "ativo": true }
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/pessoas` | Lista todas |
| GET | `/api/pessoas/{id}` | Busca por ID (404 se não existe) |
| GET | `/api/pessoas/buscar?nome=X` | Busca por nome (like) |
| POST | `/api/pessoas` | Cria (201) |
| PUT | `/api/pessoas/{id}` | Atualiza (404 se não existe) |
| PATCH | `/api/pessoas/{id}/desativar` | Soft delete — seta ativo=false (204) |
| DELETE | `/api/pessoas/{id}` | Remoção permanente (204) |

**Seed:** 4 pessoas fixas (Ana Silva, Carlos Souza, Maria Oliveira, João Teste) + 1 adicionada nos testes.

---

### 6.2 curso-service (porta 8083)

**Modelo:**
```json
{ "id": 1, "nome": "Engenharia de Software", "cargaHoraria": 3600, "ativo": true }
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/cursos` | Lista todos |
| GET | `/api/cursos/{id}` | Busca por ID |
| POST | `/api/cursos` | Cria (201) |
| PUT | `/api/cursos/{id}` | Atualiza |
| PATCH | `/api/cursos/{id}/desativar` | Soft delete (204) |
| DELETE | `/api/cursos/{id}` | Remoção permanente (204) |

**Seed:** 3 cursos (Engenharia de Software, Direito, Administração).

---

### 6.3 disciplina-service (porta 8084)

**Modelo:**
```json
{ "id": 1, "nome": "Banco de Dados", "codigo": "BD01", "ativo": true }
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/disciplinas` | Lista todas |
| GET | `/api/disciplinas/{id}` | Busca por ID |
| POST | `/api/disciplinas` | Cria (201) |
| PUT | `/api/disciplinas/{id}` | Atualiza |
| DELETE | `/api/disciplinas/{id}` | Remoção permanente (204) |

**Seed:** 3 disciplinas (Banco de Dados / BD01, Programação Java / JAVA01, Estrutura de Dados / ED01).

---

### 6.4 professor-service (porta 8085)

**Modelo:**
```json
{ "id": 1, "nome": "Carlos Silva", "especialidade": "Matemática", "ativo": true }
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/professores` | Lista todos |
| GET | `/api/professores/{id}` | Busca por ID |
| GET | `/api/professores/buscar?nome=X` | Busca por nome |
| POST | `/api/professores` | Cria (201) |
| PUT | `/api/professores/{id}` | Atualiza |
| PATCH | `/api/professores/{id}/desativar` | Soft delete (204) |
| DELETE | `/api/professores/{id}` | Remoção permanente (204) |

**Seed:** 3 professores (Carlos Silva / Matemática, Fernanda Souza / Programação, Marcos Lima / Banco de Dados).

---

### 6.5 turma-service (porta 8086)

**Modelo:**
```json
{ "id": 1, "nome": "Turma A", "semestre": "2026.1", "ativo": true }
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/turmas` | Lista todas |
| GET | `/api/turmas/{id}` | Busca por ID |
| POST | `/api/turmas` | Cria (201) |
| PUT | `/api/turmas/{id}` | Atualiza |
| DELETE | `/api/turmas/{id}` | Remoção permanente (204) |

**Seed:** 3 turmas (Turma A / 2026.1, Turma B / 2026.1, Turma C / 2026.2).

---

### 6.6 matricula-service (porta 8081)

O serviço mais complexo. Armazena apenas IDs de referência (sem `@ManyToOne`) e resolve os nomes chamando os outros serviços em tempo de execução.

**Modelo armazenado:**
```json
{ "id": 1, "pessoaId": 1, "cursoId": 1, "dataMatricula": "2024-01-10", "ativo": true }
```

**DTO enriquecido (GET /{id}):**
```json
{
  "id": 1,
  "pessoaId": 1,
  "nomePessoa": "Ana Silva",
  "cursoId": 1,
  "nomeCurso": "Engenharia de Software",
  "dataMatricula": "2024-01-10",
  "ativo": true
}
```

**Endpoints:**

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/matriculas` | Lista todas (modelo simples) |
| GET | `/api/matriculas/{id}` | Busca por ID com nomes resolvidos (DTO) |
| GET | `/api/matriculas/pessoa/{pessoaId}` | Filtra por pessoa |
| GET | `/api/matriculas/curso/{cursoId}` | Filtra por curso |
| POST | `/api/matriculas` | Cria matrícula (201) |
| PUT | `/api/matriculas/{id}` | Atualiza |
| PATCH | `/api/matriculas/{id}/desativar` | Soft delete — ativo=false (204) |
| DELETE | `/api/matriculas/{id}` | Remoção permanente (204) |

**Comunicação inter-serviços:**

Configurado em `application.properties`:
```properties
pessoa.service.url=http://pessoa-service:8082
curso.service.url=http://curso-service:8083
```

O `MatriculaService` usa `RestTemplate` para chamar esses URLs. Se o serviço estiver indisponível, o campo retorna `"indisponivel"` via fallback (não lança exceção).

**Seed:** 4 matrículas (Ana em Engenharia, Carlos em Engenharia, Ana em Direito, Maria em Direito — inativa).

---

## 7. Gateway e Segurança

### 7.1 gateway-service (porta 8080)

O gateway é a **única porta de entrada pública** do sistema de microserviços. Construído com **Spring Cloud Gateway + WebFlux** (reativo), ele:

1. Valida o JWT recebido no header `Authorization`
2. Aplica as regras de autorização (RBAC)
3. Injeta os headers `X-Auth-User` e `X-Auth-Role` para os serviços downstream
4. Roteia a requisição para o microserviço correto

**Tabela de roteamento (`application.properties`):**

| Rota | URI de destino | Path |
|---|---|---|
| auth | `http://app:8080` | `/api/auth/**` |
| matricula | `http://matricula-service:8081` | `/api/matriculas/**` |
| pessoa | `http://pessoa-service:8082` | `/api/pessoas/**` |
| curso | `http://curso-service:8083` | `/api/cursos/**` |
| disciplina | `http://disciplina-service:8084` | `/api/disciplinas/**` |
| professor | `http://professor-service:8085` | `/api/professores/**` |
| turma | `http://turma-service:8086` | `/api/turmas/**` |

### 7.2 Regras de Autorização (RBAC)

```
/api/auth/**          → público (sem token)
GET /**               → ALUNO ou PROFESSOR
POST /**              → apenas PROFESSOR
PUT /**               → apenas PROFESSOR
DELETE /**            → apenas PROFESSOR
qualquer outro        → autenticado
```

### 7.3 Validação JWT no Gateway

O gateway usa **Spring Security OAuth2 Resource Server** com `NimbusReactiveJwtDecoder`:

- Decodifica e valida o JWT com a mesma chave secreta usada pelo backend (`minha-chave-secreta-super-segura-32bytes!!`)
- Extrai o claim `role` para montar as authorities (`ROLE_PROFESSOR`, `ROLE_ALUNO`)
- A classe `JwtHeadersRelayFilter` propaga `X-Auth-User` e `X-Auth-Role` para os microserviços downstream

### 7.4 Credenciais de teste

| Username | Password | Role |
|---|---|---|
| `professor` | `prof123` | PROFESSOR (leitura + escrita) |
| `aluno` | `aluno123` | ALUNO (somente leitura) |

---

## 8. Frontend Angular

### Estrutura

```
frontend/src/app/
├── app.module.ts           # Módulo raiz (HttpClientModule)
├── app.component.*         # Componente raiz com router-outlet
├── app-routing.module.ts   # Rota /pessoas → PessoasComponent
├── pessoas.service.ts      # HTTP GET /api/pessoas
└── pessoas/
    ├── pessoas.component.ts   # Listagem de pessoas (ngOnInit → load)
    ├── pessoas.component.html # Template com *ngFor
    └── pessoas.component.css  # Estilos
```

### Comunicação com o backend

O serviço Angular faz requisições para `/api/pessoas` (path relativo). O **nginx** dentro do container faz proxy desse path para `http://gateway-service:8080`:

```nginx
location /api/ {
  proxy_pass http://gateway-service:8080/;
}
```

Isso significa que o frontend só funciona corretamente quando rodando dentro da rede Docker do compose, onde `gateway-service` é resolvível por DNS.

### Como rodar o frontend

O frontend tem seu próprio `docker-compose.yml` em `frontend/`:

```bash
cd frontend
docker compose up -d --build
# Acessa em http://localhost:4200
```

> **Atenção:** Para que o proxy nginx funcione, o frontend precisa estar na mesma rede Docker que o `gateway-service`. Conecte manualmente a rede `crudfullstackspring_microservicos-net` ou inclua o frontend no compose principal.

---

## 9. Docker e Infraestrutura

### 9.1 Portas expostas

| Porta | Serviço | Descrição |
|---|---|---|
| **8080** | gateway-service | Entrada principal — use esta porta para tudo |
| 8081 | matricula-service | Acesso direto (sem auth) |
| 8082 | pessoa-service | Acesso direto (sem auth) |
| 8083 | curso-service | Acesso direto (sem auth) |
| 8084 | disciplina-service | Acesso direto (sem auth) |
| 8085 | professor-service | Acesso direto (sem auth) |
| 8086 | turma-service | Acesso direto (sem auth) |
| **8090** | app (auth/monolito) | Backend monolito para auth |

> As portas 8081–8086 ficam expostas para facilitar o desenvolvimento. Em produção, apenas a 8080 deveria ser pública.

### 9.2 Rede Docker

Todos os containers estão na rede bridge `microservicos-net`. Isso permite que se comuniquem pelo nome do serviço:

```
gateway-service  →  http://app:8080
matricula-service →  http://pessoa-service:8082
matricula-service →  http://curso-service:8083
```

### 9.3 Limites de memória

Cada container tem `mem_limit: 350m` com JVM configurada via `JAVA_TOOL_OPTIONS`:

```
-Xmx256m -Xms128m -XX:+UseSerialGC
```

Isso mantém o consumo total em torno de **2,8 GB** para os 8 containers.

### 9.4 Dockerfiles

Todos os serviços usam build multi-stage:

```dockerfile
# Etapa 1: compila
FROM maven:3.9.6-eclipse-temurin-21 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: executa
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 10. Como Executar

### Pré-requisitos

- Docker Desktop instalado e rodando
- Portas 8080–8086 e 8090 livres

### Subir todos os serviços

```bash
# Na raiz do projeto (onde está o docker-compose.yml)
docker compose up -d --build

# Verificar se todos os 8 containers estão rodando
docker ps
```

### Parar tudo

```bash
docker compose down
```

### Rebuild de um serviço específico

```bash
# Exemplo: reconstruir apenas o backend monolito
docker compose up -d --build app
```

### Ver logs de um serviço

```bash
docker logs gateway-service
docker logs app
docker logs matricula-service
```

---

## 11. Endpoints — Referência Completa

### Via Gateway (porta 8080) — use sempre esta porta

Todos os endpoints abaixo precisam do header `Authorization: Bearer <token>`, exceto o login.

#### Autenticação

```
POST /api/auth/login     → público
POST /api/auth/register  → PROFESSOR
```

#### Pessoas

```
GET    /api/pessoas          → ALUNO/PROFESSOR
POST   /api/pessoas          → PROFESSOR
PUT    /api/pessoas/{id}     → PROFESSOR
DELETE /api/pessoas/{id}     → PROFESSOR (204)
```

#### Cursos

```
GET    /api/cursos          → ALUNO/PROFESSOR
POST   /api/cursos          → PROFESSOR
PUT    /api/cursos/{id}     → PROFESSOR
DELETE /api/cursos/{id}     → PROFESSOR (204)
```

#### Disciplinas

```
GET    /api/disciplinas          → ALUNO/PROFESSOR
POST   /api/disciplinas          → PROFESSOR
PUT    /api/disciplinas/{id}     → PROFESSOR
DELETE /api/disciplinas/{id}     → PROFESSOR (204)
```

#### Professores

```
GET    /api/professores          → ALUNO/PROFESSOR
POST   /api/professores          → PROFESSOR
PUT    /api/professores/{id}     → PROFESSOR
DELETE /api/professores/{id}     → PROFESSOR (204)
```

#### Turmas

```
GET    /api/turmas          → ALUNO/PROFESSOR
POST   /api/turmas          → PROFESSOR
PUT    /api/turmas/{id}     → PROFESSOR
DELETE /api/turmas/{id}     → PROFESSOR (204)
```

#### Matrículas

```
GET    /api/matriculas                    → ALUNO/PROFESSOR  (lista simples)
GET    /api/matriculas/{id}               → ALUNO/PROFESSOR  (DTO com nomes)
GET    /api/matriculas/pessoa/{pessoaId}  → ALUNO/PROFESSOR
GET    /api/matriculas/curso/{cursoId}    → ALUNO/PROFESSOR
POST   /api/matriculas                    → PROFESSOR
PUT    /api/matriculas/{id}               → PROFESSOR
DELETE /api/matriculas/{id}               → PROFESSOR (204)
```

---

## 12. Exemplos de Requisições

### 1. Fazer login e obter token

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"professor","password":"prof123"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "PROFESSOR"
}
```

Credenciais erradas retornam `401`:
```json
{ "error": "Usuario ou senha invalidos" }
```

### 2. Listar pessoas (autenticado)

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl http://localhost:8080/api/pessoas \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Criar um curso

```bash
curl -X POST http://localhost:8080/api/cursos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ciência da Computação","cargaHoraria":3200,"ativo":true}'
```

Resposta `201`:
```json
{ "id": 5, "nome": "Ciência da Computação", "cargaHoraria": 3200, "ativo": true }
```

### 4. Criar uma matrícula

```bash
curl -X POST http://localhost:8080/api/matriculas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pessoaId":1,"cursoId":1,"dataMatricula":"2026-06-10","ativo":true}'
```

### 5. Buscar matrícula com nomes resolvidos

```bash
curl http://localhost:8080/api/matriculas/1 \
  -H "Authorization: Bearer $TOKEN"
```

Resposta:
```json
{
  "id": 1,
  "pessoaId": 1,
  "nomePessoa": "Ana Silva",
  "cursoId": 1,
  "nomeCurso": "Engenharia de Software",
  "dataMatricula": "2024-01-10",
  "ativo": true
}
```

### 6. Deletar um registro

```bash
curl -X DELETE http://localhost:8080/api/pessoas/5 \
  -H "Authorization: Bearer $TOKEN"
# Retorna 204 No Content
```

### 7. Tentativa de escrita com role ALUNO (bloqueada)

```bash
TOKEN_ALUNO="..."   # token do aluno

curl -X POST http://localhost:8080/api/pessoas \
  -H "Authorization: Bearer $TOKEN_ALUNO" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","email":"t@t.com","ativo":true}'
# Retorna 403 Forbidden
```

### 8. Requisição sem token (bloqueada)

```bash
curl http://localhost:8080/api/pessoas
# Retorna 401 Unauthorized
```
