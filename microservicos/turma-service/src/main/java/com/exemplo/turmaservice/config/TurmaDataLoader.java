package com.exemplo.turmaservice.config;

import com.exemplo.turmaservice.model.Turma;
import com.exemplo.turmaservice.repository.TurmaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TurmaDataLoader {

    @Bean
    CommandLineRunner carregarDados(TurmaRepository repository) {

        return args -> {

            repository.save(new Turma(
                    "Turma A",
                    "2026.1",
                    true
            ));

            repository.save(new Turma(
                    "Turma B",
                    "2026.1",
                    true
            ));

            repository.save(new Turma(
                    "Turma C",
                    "2026.2",
                    true
            ));

            System.out.println("[turma-service] Dados iniciais carregados.");
        };
    }
}