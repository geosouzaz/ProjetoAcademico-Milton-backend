package com.exemplo.crudmongo.config;

import com.exemplo.crudmongo.Model.Turma;
import com.exemplo.crudmongo.repository.TurmaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TurmaDataLoader {
    @Bean
    CommandLineRunner initTurma(TurmaRepository repository) {
        return args -> {
            repository.save(new Turma(null, "Turma A", "2026.1", true));
            repository.save(new Turma(null, "Turma B", "2026.1", true));
        };
    }
}
