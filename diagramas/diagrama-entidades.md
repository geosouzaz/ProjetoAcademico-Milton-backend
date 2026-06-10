
# Atividade Prática: CRUD com Spring Boot

## Enunciado

Você está desenvolvendo uma aplicação de cadastro acadêmico utilizando Java, Spring Boot e JPA. O objetivo é praticar a criação de APIs REST completas, com operações básicas de cadastro (CRUD) para diferentes entidades do domínio escolar.

### O que você deve fazer:

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

### Dicas:
- Use nomes e tipos de atributos coerentes com o contexto de cada entidade.
- Siga o padrão de organização do projeto para facilitar a manutenção e entendimento do código.
- Não implemente pesquisa e paginação nesta branch (isso será feito em outra etapa).

---

# Diagrama de Entidades (Modelo Simplificado)

```mermaid
erDiagram
    CURSO {
        Long id
        String nome
        int cargaHoraria
        boolean ativo
    }
    PESSOA {
        Long id
        String nome
        int idade
        String email
        boolean ativo
    }
    // Outras entidades a serem criadas...
```

## Solicitação de Criação de Novas Entidades

Com base na lógica já implementada para `Curso` e `Pessoa`, crie mais 5 entidades seguindo o mesmo padrão (model, repository, service, controller, data loader, endpoints REST, pesquisa e paginação):

1. **Professor**
   - Atributos sugeridos: id, nome, especialidade, email, ativo
2. **Disciplina**
   - Atributos sugeridos: id, nome, cargaHoraria, ativo
3. **Turma**
   - Atributos sugeridos: id, nome, ano, ativo
4. **Matricula**
   - Atributos sugeridos: id, pessoaId, cursoId, dataMatricula, ativo
5. **Avaliacao**
   - Atributos sugeridos: id, pessoaId, disciplinaId, nota, data, ativo

Cada entidade deve conter:
- Model (com atributos e anotações JPA)
- Repository (interface estendendo JpaRepository)
- Service (lógica de negócio, CRUD, pesquisa, paginação)
- Controller (endpoints REST)
- DataLoader (popular dados fake para testes)

> **Prática:** Implemente cada entidade do zero, testando os endpoints e as pesquisas, para fixar o padrão de CRUD, pesquisa e paginação no Spring Boot.
