# Backend Spring Boot

CRUD com MongoDB Atlas


## Atividade Prática

Consulte o enunciado e veja o diagrama das entidades abaixo para realizar a atividade proposta:

### Enunciado

Você está desenvolvendo uma aplicação de cadastro acadêmico utilizando Java, Spring Boot e JPA. O objetivo é praticar a criação de APIs REST completas, com operações básicas de cadastro (CRUD) para diferentes entidades do domínio escolar.

#### O que você deve fazer:

1. **Estude o exemplo das entidades `Curso` e `Pessoa` já implementadas no projeto.**
   - Analise como estão organizados os arquivos Model, Repository, Service, Controller e DataLoader.
   - Observe como cada camada se comunica e como as operações básicas (listar, criar, atualizar, excluir) são implementadas.

2. **Crie mais 5 entidades seguindo exatamente o mesmo padrão:**
   - Professor
   - Disciplina
   - Turma
   - Matricula
   - Avaliacao

   Para cada entidade, implemente:
   - Model (com atributos e anotações JPA)
   - Repository (interface estendendo JpaRepository)
   - Service (lógica de negócio, CRUD)
   - Controller (endpoints REST)
   - DataLoader (popular dados fake para testes)

3. **Teste todos os endpoints utilizando o Postman ou outra ferramenta de sua preferência.**
   - Garanta que é possível criar, listar, atualizar e excluir registros de cada entidade.

4. **Documente no final do arquivo quais endpoints você criou e exemplos de uso.**

#### Dicas:
- Use nomes e tipos de atributos coerentes com o contexto de cada entidade.
- Siga o padrão de organização do projeto para facilitar a manutenção e entendimento do código.
- Não implemente pesquisa e paginação nesta branch (isso será feito em outra etapa).

---


### Diagrama das Entidades

> **Atenção:** O diagrama abaixo utiliza sintaxe Mermaid. O GitHub pode não renderizar automaticamente para todos os usuários ou tipos de diagrama. Caso não visualize o diagrama, copie o bloco abaixo e cole no [Mermaid Live Editor](https://mermaid.live/) para visualização gráfica.

```mermaid
  erDiagram
    TURMA {
        Long id
        String nome
        int ano
        boolean ativo
    }
    MATRICULA {
        Long id
        Long pessoaId
        Long cursoId
        String dataMatricula
        boolean ativo
    }
    AVALIACAO {
        Long id
        Long pessoaId
        Long disciplinaId
        double nota
        String data
        boolean ativo
    }
    PESSOA {
        Long id
        String nome
        int ano
    }
    DISCIPLINA {
        Long id
    }

        PESSOA ||--o{ MATRICULA : faz
    CURSO ||--o{ MATRICULA : possui
    PESSOA ||--o{ AVALIACAO : recebe
    DISCIPLINA ||--o{ AVALIACAO : compoe
    TURMA ||--o{ PESSOA : agrupa
    PROFESSOR ||--o{ DISCIPLINA : ministra
    
        CURSO {
            Long id
            String nome
            boolean ativo
        }
        PROFESSOR {
            Long id
            String nome
            String area
            boolean ativo
        }
---

## Endpoints Criados e Exemplos de Uso

> **Autenticação**: Todos os endpoints (exceto `/api/auth/login`) requerem um token JWT no header:
> ```
> Authorization: Bearer <token>
> ```
> Usuários padrão criados automaticamente:
> - `professor` / `prof123` → role: PROFESSOR (CRUD completo)
> - `aluno` / `aluno123` → role: ALUNO (somente leitura)

### 🔑 Autenticação

#### `POST /api/auth/login`
Retorna o token JWT para autenticação.
```json
// Body
{ "username": "professor", "password": "prof123" }

// Response
{ "token": "eyJhbGci...", "role": "PROFESSOR" }
```

#### `POST /api/auth/register` *(requer role: PROFESSOR)*
Registra um novo usuário.
```json
// Body
{ "username": "novoaluno", "password": "senha123", "role": "ALUNO" }

// Response
{ "message": "Usuario registrado com sucesso" }
```

---

### 👨‍🏫 Professor — `/professores`

| Método | Endpoint            | Acesso             | Descrição              |
|--------|---------------------|--------------------|------------------------|
| GET    | `/professores`      | PROFESSOR, ALUNO   | Lista todos            |
| GET    | `/professores/{id}` | PROFESSOR, ALUNO   | Busca por ID           |
| POST   | `/professores`      | PROFESSOR          | Cria novo professor    |
| PUT    | `/professores/{id}` | PROFESSOR          | Atualiza por ID        |
| DELETE | `/professores/{id}` | PROFESSOR          | Remove por ID          |

```json
// POST /professores - Body
{
  "nome": "Ana Lima",
  "idade": 40,
  "email": "ana@escola.com",
  "area": "Fisica",
  "ativo": true
}
```

---

### 📚 Disciplina — `/disciplinas`

| Método | Endpoint              | Acesso             | Descrição              |
|--------|-----------------------|--------------------|------------------------|
| GET    | `/disciplinas`        | PROFESSOR, ALUNO   | Lista todas            |
| GET    | `/disciplinas/{id}`   | PROFESSOR, ALUNO   | Busca por ID           |
| POST   | `/disciplinas`        | PROFESSOR          | Cria nova disciplina   |
| PUT    | `/disciplinas/{id}`   | PROFESSOR          | Atualiza por ID        |
| DELETE | `/disciplinas/{id}`   | PROFESSOR          | Remove por ID          |

```json
// POST /disciplinas - Body
{
  "nome": "Fisica Quantica",
  "ativo": true
}
```

---

### 🏫 Turma — `/turmas`

| Método | Endpoint          | Acesso             | Descrição           |
|--------|-------------------|--------------------|---------------------|
| GET    | `/turmas`         | PROFESSOR, ALUNO   | Lista todas         |
| GET    | `/turmas/{id}`    | PROFESSOR, ALUNO   | Busca por ID        |
| POST   | `/turmas`         | PROFESSOR          | Cria nova turma     |
| PUT    | `/turmas/{id}`    | PROFESSOR          | Atualiza por ID     |
| DELETE | `/turmas/{id}`    | PROFESSOR          | Remove por ID       |

```json
// POST /turmas - Body
{
  "nome": "Turma C",
  "ano": 2025,
  "ativo": true
}
```

---

### 📋 Matricula — `/matriculas`

| Método | Endpoint             | Acesso             | Descrição              |
|--------|----------------------|--------------------|------------------------|
| GET    | `/matriculas`        | PROFESSOR, ALUNO   | Lista todas            |
| GET    | `/matriculas/{id}`   | PROFESSOR, ALUNO   | Busca por ID           |
| POST   | `/matriculas`        | PROFESSOR          | Cria nova matricula    |
| PUT    | `/matriculas/{id}`   | PROFESSOR          | Atualiza por ID        |
| DELETE | `/matriculas/{id}`   | PROFESSOR          | Remove por ID          |

```json
// POST /matriculas - Body
{
  "pessoaId": 1,
  "cursoId": 1,
  "dataMatricula": "2025-02-01",
  "ativo": true
}
```

---

### 📝 Avaliacao — `/avaliacoes`

| Método | Endpoint             | Acesso             | Descrição              |
|--------|----------------------|--------------------|------------------------|
| GET    | `/avaliacoes`        | PROFESSOR, ALUNO   | Lista todas            |
| GET    | `/avaliacoes/{id}`   | PROFESSOR, ALUNO   | Busca por ID           |
| POST   | `/avaliacoes`        | PROFESSOR          | Cria nova avaliacao    |
| PUT    | `/avaliacoes/{id}`   | PROFESSOR          | Atualiza por ID        |
| DELETE | `/avaliacoes/{id}`   | PROFESSOR          | Remove por ID          |

```json
// POST /avaliacoes - Body
{
  "pessoaId": 1,
  "disciplinaId": 2,
  "nota": 9.5,
  "data": "2025-06-10",
  "ativo": true
}
```

---

### ℹ️ Endpoints já existentes

- `GET/POST/PUT/DELETE /cursos` — Entidade Curso
- `GET/POST/PUT/DELETE /pessoas` — Entidade Pessoa
