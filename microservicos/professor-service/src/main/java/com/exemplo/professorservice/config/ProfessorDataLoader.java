package com.exemplo.professorservice.config;

import com.exemplo.professorservice.model.Professor;
import com.exemplo.professorservice.repository.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfessorDataLoader {

    @Bean
    CommandLineRunner carregarDados(ProfessorRepository repository) {
        return args -> {

            repository.save(new Professor(
                    "Carlos Silva",
                    "Matemática",
                    true
            ));

            repository.save(new Professor(
                    "Fernanda Souza",
                    "Programação",
                    true
            ));

            repository.save(new Professor(
                    "Marcos Lima",
                    "Banco de Dados",
                    true
            ));

            System.out.println("[professor-service] Dados iniciais carregados.");
        };
    }
}