# Diagrama de Sequência ? Boot do CrudMongoApplication

```mermaid
sequenceDiagram
    autonumber
    participant JVM as JVM
    participant Main as CrudMongoApplication.main()
    participant Spring as SpringApplication
    participant Context as ApplicationContext (IoC)
    participant AutoConf as Auto-Configuration
    participant JPA as JPA / Hibernate
    participant H2 as H2 (banco em memória)
    participant Security as SecurityConfig
    participant JWT as JwtAuthFilter
    participant Runners as CommandLineRunners

    JVM->>Main: inicia execução
    Main->>Spring: SpringApplication.run(CrudMongoApplication.class, args)

    rect rgb(230, 240, 255)
        Note over Spring,Context: Fase 1 ? Criação do ApplicationContext
        Spring->>Context: cria ApplicationContext
        Spring->>Context: component scan em com.exemplo.crudmongo
        Context->>AutoConf: dispara @EnableAutoConfiguration
    end

    rect rgb(255, 245, 220)
        Note over AutoConf,H2: Fase 2 ? Configuração do DataSource e JPA
        AutoConf->>H2: conecta via jdbc:h2:mem:crudmongo (application.properties)
        AutoConf->>JPA: inicializa Hibernate (H2Dialect)
        JPA->>JPA: lê classes anotadas com @Entity
        Note right of JPA: Pessoa, Curso, Disciplina,<br/>Turma, Matricula, Avaliacao,<br/>Professor, Usuario
        JPA->>H2: ddl-auto=update ? CREATE TABLE / ALTER TABLE
        H2-->>JPA: tabelas criadas/atualizadas
    end

    rect rgb(230, 255, 230)
        Note over Context,JWT: Fase 3 ? Configuração de Segurança
        Context->>Security: instancia SecurityConfig (@Configuration)
        Security->>JWT: injeta JwtAuthFilter (@Autowired)
        Security->>Context: registra SecurityFilterChain (STATELESS)
        Note right of Security: /api/auth/** ? livre<br/>demais rotas ? JWT obrigatório
    end

    rect rgb(255, 230, 230)
        Note over Context,Runners: Fase 4 ? Execução dos CommandLineRunners (seed de dados)
        Context->>Runners: dispara todos os CommandLineRunner @Beans

        Runners->>H2: DataLoader ? 200 Cursos (se vazio)
        Runners->>H2: PessoaDataLoader ? 500.000 Pessoas (se vazio)
        Runners->>H2: ProfessorDataLoader ? seed de Professores
        Runners->>H2: DisciplinaDataLoader ? seed de Disciplinas
        Runners->>H2: TurmaDataLoader ? Turma A / Turma B
        Runners->>H2: MatriculaDataLoader ? seed de Matrículas
        Runners->>H2: AvaliacaoDataLoader ? seed de Avaliações
        H2-->>Runners: dados persistidos
    end

    Context-->>Spring: contexto pronto
    Spring-->>Main: aplicação no ar na porta 8080
```

## Notas explicativas

| Fase | Responsável | O que acontece |
|------|-------------|----------------|
| 1 ? IoC Container | `@SpringBootApplication` | Escaneia todos os pacotes filhos e registra beans (`@Component`, `@Service`, `@Repository`, `@Configuration`) |
| 2 ? JPA/Hibernate | `spring.jpa.hibernate.ddl-auto=update` | Hibernate compara as `@Entity` com o banco H2 e emite DDL para criar ou ajustar as tabelas |
| 3 ? Security | `SecurityConfig` + `JwtAuthFilter` | Monta a cadeia de filtros HTTP; todo request passará pelo filtro JWT antes de chegar aos controllers |
| 4 ? Data Seed | `CommandLineRunner` em cada `*DataLoader` | Após o contexto estar 100% pronto, cada loader verifica se a coleção está vazia e insere dados de teste com Faker |
