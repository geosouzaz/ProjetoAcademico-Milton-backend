package com.exemplo.matriculaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================
 * MICROSERVICO: matricula-service
 * =====================================================
 *
 * Este e um servico independente responsavel APENAS
 * pelo gerenciamento de matriculas.
 *
 * Para executar:
 *   mvn spring-boot:run
 *
 * Acesso:
 *   API:        http://localhost:8081/api/matriculas
 *   H2 Console: http://localhost:8081/h2-console
 *
 * =====================================================
 * DIFERENCAS em relacao ao monolito:
 * =====================================================
 *
 * MONOLITO                        | MICROSERVICO
 * --------------------------------|----------------------------
 * Uma aplicacao, todas entidades  | Uma aplicacao por entidade
 * Um banco compartilhado          | Banco exclusivo (matriculadb)
 * Porta 8080                      | Porta 8081
 * Comunica internamente (chamada  | Comunica via HTTP REST
 *   de metodo Java)               |   com outros servicos
 *
 * =====================================================
 */
@SpringBootApplication
public class MatriculaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatriculaServiceApplication.class, args);
    }
}
